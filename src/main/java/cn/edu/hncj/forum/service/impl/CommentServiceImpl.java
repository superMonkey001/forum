package cn.edu.hncj.forum.service.impl;

import cn.edu.hncj.forum.dto.CommentReturnDTO;
import cn.edu.hncj.forum.enums.CommentTypeEnum;
import cn.edu.hncj.forum.exception.CustomizeErrorCode;
import cn.edu.hncj.forum.exception.CustomizeException;
import cn.edu.hncj.forum.mapper.*;
import cn.edu.hncj.forum.model.Comment;
import cn.edu.hncj.forum.model.CommentExample;
import cn.edu.hncj.forum.model.Question;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FanJian
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(Comment comment) {
        // 如果父级评论或父级问题被删除
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            // 因为抛出异常后，会返回当前页面一个JSON字符串（逻辑在CustomizeExceptionHandler类中封装）
            // 抛出异常，让上一级Controller接收到{2002,"未选中任何问题或评论进行回复"}
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        Integer commentType = comment.getType();
        if (commentType == null || !CommentTypeEnum.isExist(commentType)) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_ERROR);
        }

        // 如果评论的是一级评论
        if (commentType.equals(CommentTypeEnum.COMMENT.getType())) {
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            // 如果在评论一级评论的时候，一级评论被删除了
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            } else {
                // 向数据库中插入这条评论的数据
                commentMapper.insert(comment);
                // 为父级评论的评论数加一
                Comment parentComment = new Comment();
                parentComment.setId(comment.getParentId());
                // 现阶段设置成1，以后添加缓存功能的时候，可能会将参数改大.(set COMMENT_COUNT = COMMENT_COUNT + #{commentCount})
                parentComment.setCommentCount(1);
                commentExtMapper.incCommentCount(parentComment);
            }
        }// 如果评论的是问题
        else if (commentType.equals(CommentTypeEnum.QUESTION.getType())) {
            Question dbQuestion = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (dbQuestion == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            } else {
                commentMapper.insert(comment);
                dbQuestion.setCommentCount(1);
                questionExtMapper.incCommentCount(dbQuestion);
            }

        }
    }

    @Override
    public List<CommentReturnDTO> listByParentId(Long parentId, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        // 注意判断当前评论的type是1（评论Question），还是2（评论Comment）
        commentExample.createCriteria().andParentIdEqualTo(parentId).andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        List<CommentReturnDTO> commentReturnDTOS = copyList(comments);
        return commentReturnDTOS;
    }

    private CommentReturnDTO copy(Comment comment) {
        CommentReturnDTO commentReturnDTO = new CommentReturnDTO();
        BeanUtils.copyProperties(comment,commentReturnDTO);
        User user = userMapper.selectByPrimaryKey(comment.getCommentator());
        commentReturnDTO.setUser(user);
        return commentReturnDTO;
    }

    private List<CommentReturnDTO> copyList(List<Comment> comments) {
        List<CommentReturnDTO> commentReturnDTOS = new ArrayList<>();
        for (Comment comment : comments) {
            commentReturnDTOS.add(copy(comment));
        }
        return commentReturnDTOS;
    }
}
