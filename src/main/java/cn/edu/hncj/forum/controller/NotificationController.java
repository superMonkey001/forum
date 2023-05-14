package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.NotificationDTO;
import cn.edu.hncj.forum.enums.NotificationTypeEnum;
import cn.edu.hncj.forum.model.User;
import cn.edu.hncj.forum.notify.entity.Message;
import cn.edu.hncj.forum.notify.service.MessageService;
import cn.edu.hncj.forum.service.NotificationService;
import cn.edu.hncj.forum.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

import static cn.edu.hncj.forum.notify.constant.CommunityConstant.TOPIC_COMMENT;

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

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @RequestMapping(path = "notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model,User user) {
        //查询评论类通知
        Message message = messageService.findLatestMessage(Math.toIntExact(user.getId()), TOPIC_COMMENT);
        Map<String, Object> messageVO = new HashMap<>();
        if (message != null) {
            messageVO.put("message", message);
            String content = message.getContent();
            //{&quot;entityType&quot;:1,&quot;entityId&quot;:275,&quot;postId&quot;:275,&quot;userId&quot;:111}
            content = HtmlUtils.htmlUnescape(content);  //这步可以去除上方转义字符
            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            //谁发给我
            // messageVO.put("user", userService.findUserById((int) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));
            int count = messageService.findNoticeCount(Math.toIntExact(user.getId()), TOPIC_COMMENT);
            messageVO.put("count", count);
            int unread = messageService.findNoticeUnreadCount(Math.toIntExact(user.getId()), TOPIC_COMMENT);
            messageVO.put("unread", unread);
        }
        model.addAttribute("commentNotice", messageVO);

        /// 查询未读消息数量
        /// int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        /// model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(Math.toIntExact(user.getId()), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "site/notice";
    }

}
