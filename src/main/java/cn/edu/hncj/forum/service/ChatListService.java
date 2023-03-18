package cn.edu.hncj.forum.service;


import cn.edu.hncj.forum.model.ChatList;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Author FanJian
 * @Date 2023-03-18 16:19
 */
public interface ChatListService {
    List<ChatList> selectChat(Long linkId);

    public void insertChat(Long linkId, Long fromId, String content, Timestamp timestamp);
}
