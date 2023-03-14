package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.cache.HistoryCache;
import cn.edu.hncj.forum.dto.PaginationDTO;
import cn.edu.hncj.forum.dto.QuestionDTO;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.NotificationService;
import cn.edu.hncj.forum.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ProfileController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable("action") String action,
                          HttpServletRequest request,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "8") Integer size,
                          Model model) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        // 如果用户点击的是“我的问题”
        if ("questions".equals(action)) {
            // user : question = 1 : n 一个用户可以有多个问题
            // 通过user的id关联到question表
            PaginationDTO paginationDTO = questionService.list(user.getId(), page, size);
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            model.addAttribute("pagination", paginationDTO);
        }// 如果用户点击的是“最新回复”
        else if ("replies".equals(action)) {
            PaginationDTO paginationDTO = notificationService.list(user.getId(), page, size);
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
            model.addAttribute("pagination", paginationDTO);
        }// 如果用户点击的是“浏览历史”
        else if ("histories".equals(action)) {
            HistoryCache historyCache = (HistoryCache) request.getSession().getAttribute("historyCache");
            HistoryCache.DoubleLinkedList linkedList = historyCache.linkedList;

            // 遍历链表，然后加进List中
            List<QuestionDTO> questionDTOs = new ArrayList<>();
            HistoryCache.Node node = linkedList.tail.pre;
            while (node != linkedList.head) {
                questionDTOs.add((QuestionDTO) node.val);
                node = node.pre;
            }

            PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO();
            paginationDTO.setData(questionDTOs);
            paginationDTO.setPage(0);
            paginationDTO.setTotalPage(1);
            paginationDTO.setShowPrevious(false);
            paginationDTO.setShowNext(false);
            paginationDTO.setShowFirstPage(false);
            paginationDTO.setShowEndPage(false);
            model.addAttribute("section","histories");
            model.addAttribute("sectionName","历史记录");
            model.addAttribute("pagination", paginationDTO);
        }

        return "profile";
    }
}
