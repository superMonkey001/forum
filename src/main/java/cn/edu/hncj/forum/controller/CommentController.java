package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.CommentDTO;
import cn.edu.hncj.forum.mapper.CommentMapper;
import cn.edu.hncj.forum.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author FanJian
 */
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ResponseBody
    @PostMapping("/comment")
    public Object comment(@RequestBody CommentDTO commentDTO) {
        return null;
    }
}
