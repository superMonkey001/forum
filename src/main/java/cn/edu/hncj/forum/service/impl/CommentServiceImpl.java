package cn.edu.hncj.forum.service.impl;

import cn.edu.hncj.forum.dto.CommentDTO;
import cn.edu.hncj.forum.exception.CustomizeErrorCode;
import cn.edu.hncj.forum.exception.CustomizeException;
import cn.edu.hncj.forum.exception.ICustomizeErrorCode;
import cn.edu.hncj.forum.mapper.CommentMapper;
import cn.edu.hncj.forum.mapper.QuestionMapper;
import cn.edu.hncj.forum.model.Comment;
import cn.edu.hncj.forum.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public void insert(Comment comment) {
        // 如果父级评论或父级问题被删除
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            // 抛出异常，让上一级Controller接收到{2002,"未选中任何问题或评论进行回复"}
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
    }
}
