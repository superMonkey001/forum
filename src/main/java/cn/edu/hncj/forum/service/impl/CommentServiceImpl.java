package cn.edu.hncj.forum.service.impl;

import cn.edu.hncj.forum.dto.CommentDTO;
import cn.edu.hncj.forum.enums.CommentTypeEnum;
import cn.edu.hncj.forum.exception.CustomizeErrorCode;
import cn.edu.hncj.forum.exception.CustomizeException;
import cn.edu.hncj.forum.exception.ICustomizeErrorCode;
import cn.edu.hncj.forum.mapper.CommentMapper;
import cn.edu.hncj.forum.mapper.QuestionExtMapper;
import cn.edu.hncj.forum.mapper.QuestionMapper;
import cn.edu.hncj.forum.model.Comment;
import cn.edu.hncj.forum.model.Question;
import cn.edu.hncj.forum.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author FanJian
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;


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
                commentMapper.insert(comment);
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
}
