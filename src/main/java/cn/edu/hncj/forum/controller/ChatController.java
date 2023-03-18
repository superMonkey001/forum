package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * @Author FanJian
 * @Date 2023-03-18 22:33
 */

@Controller
@RequestMapping("/chat")
public class ChatController {

    /**
     * 只有用户登录了，别人的头像才能显示链接，并且附上recId
     * 因此这个接口不用判断session中是否有用户，肯定是有的
     */
    @GetMapping
    public String chat(@RequestParam("recId") Long recId, HttpSession session) {
        session.setAttribute("recId",recId);
        Long sendId = ((User) session.getAttribute("user")).getId();
        session.setAttribute("sendId", sendId);
        return "chat";
    }
}
