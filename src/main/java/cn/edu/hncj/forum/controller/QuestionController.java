package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.CommentReturnDTO;
import cn.edu.hncj.forum.dto.QuestionDTO;
import cn.edu.hncj.forum.enums.CommentTypeEnum;
import cn.edu.hncj.forum.service.CommentService;
import cn.edu.hncj.forum.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("question/{id}")
    public String question(@PathVariable("id") Long id,
                           Model model) {
        QuestionDTO questionDTO = questionService.findById(id);
        model.addAttribute("question",questionDTO);

        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        model.addAttribute("relatedQuestions",relatedQuestions);
        // 增加阅读数
        questionService.incView(id);

        // 查询评论
        List<CommentReturnDTO> commentDTOS = commentService.listByParentId(id, CommentTypeEnum.QUESTION);
        model.addAttribute("comments",commentDTOS);

        return "question";
    }
}
