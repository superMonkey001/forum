package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.QuestionDTO;
import cn.edu.hncj.forum.mapper.QuestionExtMapper;
import cn.edu.hncj.forum.mapper.QuestionMapper;
import cn.edu.hncj.forum.model.Question;
import cn.edu.hncj.forum.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @GetMapping("question/{id}")
    public String question(@PathVariable("id") Integer id,
                           Model model) {
        QuestionDTO questionDTO = questionService.findById(id);
        model.addAttribute("question",questionDTO);

        // 增加阅读数
        questionExtMapper.incView(id);
        return "question";
    }
}
