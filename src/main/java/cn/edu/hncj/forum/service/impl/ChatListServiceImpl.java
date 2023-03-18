package cn.edu.hncj.forum.service.impl;

import cn.edu.hncj.forum.service.ChatListService;
import org.springframework.stereotype.Service;
import cn.edu.hncj.forum.mapper.ChatListMapper;
import cn.edu.hncj.forum.model.ChatList;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.List;
/**
 * @Author FanJian
 * @Date 2023-03-18 16:19
 */
@Service
public class ChatListServiceImpl implements ChatListService {

    @Autowired
    private ChatListMapper chatListMapper;

    public List<ChatList> selectChat(Long linkId){
        return chatListMapper.selectChat(linkId);
    }

    public void insertChat(Long linkId, Long fromId, String content, Timestamp timestamp) {
        chatListMapper.insertChat(linkId,fromId,content,timestamp);
    }
}
