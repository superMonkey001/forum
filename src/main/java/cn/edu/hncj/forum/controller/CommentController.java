package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.CommentParamDTO;
import cn.edu.hncj.forum.dto.CommentReturnDTO;
import cn.edu.hncj.forum.dto.ResultDTO;
import cn.edu.hncj.forum.enums.CommentTypeEnum;
import cn.edu.hncj.forum.exception.CustomizeErrorCode;
import cn.edu.hncj.forum.exception.CustomizeException;
import cn.edu.hncj.forum.model.Comment;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author FanJian
 */
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ResponseBody
    @PostMapping("/comment")
    public ResultDTO comment(@RequestBody CommentParamDTO commentParamDTO,
                          HttpServletRequest request) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // 返回页面Json串
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if (StringUtils.isBlank(commentParamDTO.getContent())) {
            throw new CustomizeException(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setCommentator(user.getId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setContent(commentParamDTO.getContent());
        comment.setParentId(commentParamDTO.getParentId());
        comment.setType(commentParamDTO.getType());
        comment.setLikeCount(0L);
        comment.setCommentCount(0);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }

    @ResponseBody
    @GetMapping("/comment/{id}")
    public ResultDTO<List<CommentReturnDTO>> comments(@PathVariable Long id){
        List<CommentReturnDTO> commentDTOS = commentService.listByParentId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }

}
