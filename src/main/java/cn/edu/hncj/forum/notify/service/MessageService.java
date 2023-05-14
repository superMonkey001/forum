package cn.edu.hncj.forum.notify.service;

import cn.edu.hncj.forum.notify.entity.Message;
import cn.edu.hncj.forum.notify.mapper.MessageMapper;
import cn.edu.hncj.forum.notify.wrapper.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author FanJian
 * @Date 2023-05-14 20:10
 */

public class MessageService {

    @Autowired
    private MessageMapper messageMapper;
    public void addMessage(Message message) {

    }
    //查询某个主题下最新的通知
    public Message findLatestMessage(int userId, String topic){
        Integer max = messageMapper.selectLaterNotice(userId, topic);
        if (max == null){
            return null;
        }
        return null;
    }
    //查询某个主题所包含的通知数量
    public int findNoticeCount(int userId,String topic){
        return 0;
    }
    //查询未读的通知数量
    public int findNoticeUnreadCount(int userId,String topic){
        return 0;
    }
}
