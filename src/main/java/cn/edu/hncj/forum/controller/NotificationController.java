package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.NotificationDTO;
import cn.edu.hncj.forum.enums.NotificationTypeEnum;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String notification(HttpServletRequest request, @PathVariable("id") Long id) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        NotificationDTO notificationDTO = notificationService.read(id, user);
        if (NotificationTypeEnum.REPLY_QUESTION.getType().equals(notificationDTO.getType()) || NotificationTypeEnum.REPLY_COMMENT.getType().equals(notificationDTO.getType())) {
            return "redirect:/question/" + notificationDTO.getOuterid();
        } else {
            return "redirect:/";
        }
    }
}
