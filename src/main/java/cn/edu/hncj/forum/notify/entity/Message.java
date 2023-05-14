package cn.edu.hncj.forum.notify.entity;

import java.util.Date;

/**
 * @Author FanJian
 * @Date 2023-05-14 20:08
 */

public class Message {
    int fromId;
    int entityUserId;
    String topic;
    Date date;
    int status;

    public void setFromId(int i) {
    }

    public void setToId(int entityUserId) {
    }

    public void setConversationId(String topic) {
    }

    public void setCreateTime(Date date) {
    }

    public void setStatus(int i) {
    }

    public int getFromId() {
        return fromId;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public String getTopic() {
        return topic;
    }

    public Date getDate() {
        return date;
    }

    public int getStatus() {
        return status;
    }

    public void setContent(String toJSONString) {

    }

    public String getContent() {
        return null;
    }
}
