package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.mapper.QuestionMapper;
import cn.edu.hncj.forum.mapper.UserMapper;
import cn.edu.hncj.forum.model.Question;
import cn.edu.hncj.forum.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/publish")
    public String publish() {
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(@RequestParam("title") String title,
                            @RequestParam("description") String description,
                            @RequestParam("tag") String tag,
                            HttpServletRequest request,
                            Model model) {

        // 第一时间把用户传入的信息写入model传递给publish.html，主要是用来做提交失败后，回显的功能
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);

        // 前端的写入校验。
        if(title == null || "".equals(title)) {
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if(description == null || "".equals(description)) {
            model.addAttribute("error","问题描述不能为空");
            return "publish";
        }
        if(tag == null || "".equals(tag)) {
            model.addAttribute("error","标签不能为空");
            return "publish";
        }

        //校验用户是否存在，存在就把user存入session，不存在则把错误信息传给前端。
        User user = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            // 如果当前cookie为token
            if("token".equals(cookie.getName())) {
                String token = cookie.getValue();
                user = userMapper.findUserByToken(token);
                // 如果用户换了一个浏览器（重启浏览器），才会导致在有token的情况下，查询的user为null
                if(user != null) {
                    request.getSession().setAttribute("user",user);
                }
                break;
            }
        }
        if(user == null) {
            model.addAttribute("error","用户未登录");
            return "publish";
        }


        // 如果当前用户为登录状态，则把用户写的问题信息存入到数据库中
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        questionMapper.create(question);
        return "redirect:/";
    }

}
