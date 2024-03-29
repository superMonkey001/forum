package cn.edu.hncj.forum.service;

import cn.edu.hncj.forum.dto.CommentReturnDTO;
import cn.edu.hncj.forum.enums.CommentTypeEnum;
import cn.edu.hncj.forum.model.Comment;
import cn.edu.hncj.forum.model.User;

import java.util.List;

public interface CommentService {

    void insert(Comment comment, User user);

    /**
     * @param parentId 通过父级id查找评论
     * @param type
     * @return 返回所有id为parentId的问题或回复的评论
     */
    List<CommentReturnDTO> listByParentId(Long parentId, CommentTypeEnum type);
}
