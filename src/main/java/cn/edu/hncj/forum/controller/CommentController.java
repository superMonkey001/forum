package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.CommentDTO;
import cn.edu.hncj.forum.dto.ResultDTO;
import cn.edu.hncj.forum.exception.CustomizeErrorCode;
import cn.edu.hncj.forum.exception.CustomizeException;
import cn.edu.hncj.forum.mapper.CommentMapper;
import cn.edu.hncj.forum.model.Comment;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author FanJian
 */
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ResponseBody
    @PostMapping("/comment")
    public Object comment(@RequestBody CommentDTO commentDTO,
                          HttpServletRequest request) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }

        Comment comment = new Comment();
        comment.setCommentator(user.getId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setContent(commentDTO.getContent());
        comment.setParentId(commentDTO.getParentId());
        comment.setType(commentDTO.getType());
        comment.setLikeCount(0L);
        commentService.insert(comment);
        return null;
    }
}
