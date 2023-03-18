package cn.edu.hncj.forum.model;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Author FanJian
 * @Date 2023-03-18 16:16
 */

@Data
public class ChatList {
    private int listId;
    private int linkId;
    private Long userId;
    private String content;
    private Timestamp createtime;
}
