package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.cache.HotTagCache;
import cn.edu.hncj.forum.dto.PaginationDTO;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.NotificationService;
import cn.edu.hncj.forum.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private HotTagCache hotTagCache;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "8") Integer size,
                        @RequestParam(name = "search", required = false) String search,
                        @RequestParam(name = "hotTag", required = false) String hotTag) {
        PaginationDTO pagination = questionService.list(search, hotTag, page, size);
        List<String> hotTagList = hotTagCache.getHotTagList();
        model.addAttribute("hotTags",hotTagList);
        model.addAttribute("hotTag",hotTag);
        model.addAttribute("pagination", pagination);
        model.addAttribute("search", search);
        return "index";
    }
}
