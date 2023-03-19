package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.exception.CustomizeErrorCode;
import cn.edu.hncj.forum.exception.CustomizeException;
import cn.edu.hncj.forum.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Author FanJian
 * @Date 2023-03-18 22:33
 */

@Controller
@Slf4j
@RequestMapping("/chat")
public class ChatController {

    @Value("${netty.ws}")
    private String ws;
    /**
     * 只有用户登录了，别人的头像才能显示链接，并且附上recId
     * 因此这个接口不用判断session中是否有用户，肯定是有的
     * (注：后面发现了一种session中无用户的可能，就是服务器重启了)
     *
     * 2023年3月18日23:27:53
     * 同一个浏览器中，第二个账号登录会把session中的user覆盖掉，也就相当于把第一个账号给下线了
     */
    @GetMapping
    public String chat(@RequestParam("recId") Long recId, HttpServletRequest request) {
        Long sendId = ((User) request.getSession().getAttribute("user")).getId();
        if (recId != null && recId.equals(sendId)) {
            log.info("不能和自己聊天");
            throw new CustomizeException(CustomizeErrorCode.NOT_ALLOW_TALK_YOURSELF);
        } else {
            request.setAttribute("ws", ws);
            request.setAttribute("recId",recId);
            request.setAttribute("sendId", sendId);
            return "chat";
        }
    }
}
