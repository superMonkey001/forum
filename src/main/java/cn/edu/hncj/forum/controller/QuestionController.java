package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.CommentReturnDTO;
import cn.edu.hncj.forum.dto.QuestionDTO;
import cn.edu.hncj.forum.enums.CommentTypeEnum;
import cn.edu.hncj.forum.service.CommentService;
import cn.edu.hncj.forum.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("question/{id}")
    public String question(@PathVariable("id") Long id,
                           Model model,
                           HttpServletRequest request) {
        QuestionDTO questionDTO = questionService.findById(id);
        model.addAttribute("question", questionDTO);

        // 查询相关问题
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        model.addAttribute("relatedQuestions", relatedQuestions);
        // 增加阅读数
        questionService.incView(id);

        // 查询评论
        List<CommentReturnDTO> commentDTOS = commentService.listByParentId(id, CommentTypeEnum.QUESTION);
        model.addAttribute("comments", commentDTOS);

        // 获取用户未登录但评论的内容
        // 如果为空，说明用户有登陆或者是评论内容为空
        HttpSession session = request.getSession();
        String noLoginComment = (String) session.getAttribute("noLoginComment");
        // 如果评论内容不为空或者用户是未登陆状态
        if (StringUtils.isNotBlank(noLoginComment)) {
            // 回显未登录但评论的内容
            model.addAttribute("noLoginComment",noLoginComment);
            // 记得要删除cookie中的未登录但评论的内容
            session.removeAttribute("noLoginComment");
        }

        return "question";
    }
}
