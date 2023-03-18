package cn.edu.hncj.forum.mapper;

import cn.edu.hncj.forum.model.ChatList;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Author FanJian
 * @Date 2023-03-18 16:18
 */

public interface ChatListMapper {
    List<ChatList> selectChat(Long linkId);

    void insertChat(Long linkId, Long fromId, String content, Timestamp timestamp);
}

