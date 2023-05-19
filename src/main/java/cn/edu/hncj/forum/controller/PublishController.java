package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.ac_automation.ACAutomation;
import cn.edu.hncj.forum.cache.TagCache;
import cn.edu.hncj.forum.dto.QuestionDTO;
import cn.edu.hncj.forum.model.Question;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

/**
 * @author FanJian
 */
@Controller
public class PublishController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private ACAutomation acAutomation;

    /**
     * 通过编辑按钮完成更新后，发布
     *
     * @param id 问题的id
     * @return 返回页面
     */
    @GetMapping("/publish/{id}")
    public String publish(@PathVariable("id") Long id, Model model) {
        QuestionDTO question = questionService.findById(id);
        // 第一时间把用户传入的信息写入model传递给publish.html，主要是用来做提交失败后，回显的功能
        model.addAttribute("title", question.getTitle());
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag", question.getTag());
        model.addAttribute("tags", TagCache.get());
        // 隐藏的id,用来判断问题是否已经存在
        model.addAttribute("id", question.getId());

        return "publish";
    }

    @GetMapping("/publish")
    public String publish(Model model) {
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    /**
     * 完成问题的发布
     *
     * @param id 问题的id
     */
    @PostMapping("/publish")
    public String doPublish(@RequestParam("title") String title,
                            @RequestParam("description") String description,
                            @RequestParam("tag") String tag,
                            @RequestParam(name = "id", required = false) Long id,
                            HttpServletRequest request,
                            Model model) {

        // 第一时间把用户传入的信息写入model传递给publish.html，主要是用来做提交失败后，回显的功能
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", TagCache.get());

        // 前端的写入校验。
        if (StringUtils.isBlank(title)) {
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if (StringUtils.isBlank(description)) {
            model.addAttribute("error", "问题描述不能为空");
            return "publish";
        }
        if (StringUtils.isBlank(tag)) {
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }

        // StringUtils的split方法会将形如"java,,spring"这样的字符串转换成["java","spring"]这样的字符串数组,直接去掉了逗号之间的""
        String[] split = tag.split( ",");
        if (TagCache.checkBlank(split)) {
            model.addAttribute("error", "传入了空标签");
            return "publish";
        }

        String invalid = TagCache.filterInvalid(split);
        if (StringUtils.isNotBlank(invalid)) {
            model.addAttribute("error", "输入了非法标签" + invalid + "，或者标签中含有空格");
            return "publish";
        }


        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            model.addAttribute("error", "用户未登录");
            return "publish";
        }

        Set<String> strings = acAutomation.containsWords(description);
        if (strings != null && strings.size() > 0) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("包含不文明词汇:");
            for (String str : strings) {
                errorMsg.append(" " + str + " ");
            }
            model.addAttribute("error", errorMsg);
            return "publish";
        }

        // 如果当前用户为登录状态，则把用户写的问题信息存入到数据库中
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        // id是从<input type="hidden" name="id">这个标签传过来的，如果没有经过/publish/{id}这个请求路径（方法），那么这个id就为Null
        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";
    }

}
