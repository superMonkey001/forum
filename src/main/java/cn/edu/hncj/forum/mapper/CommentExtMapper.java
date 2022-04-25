package cn.edu.hncj.forum.mapper;

import cn.edu.hncj.forum.model.Comment;
import cn.edu.hncj.forum.model.CommentExample;
import cn.edu.hncj.forum.model.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {
    /**
     * 通过回复id,增加评论数
     */
    int incCommentCount(Comment comment);
}