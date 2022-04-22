package cn.edu.hncj.forum.service;

import cn.edu.hncj.forum.dto.CommentDTO;
import cn.edu.hncj.forum.model.Comment;

public interface CommentService {
    void insert(Comment comment);
}
