package cn.edu.hncj.forum.notify.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class Event {
    //张三给李四点赞----userId是张三，entityUserId是李四
    //主题
    private String topic;
    //点赞人id
    private int userId;
    //实体类型（评论、回复）
    private int entityType;
    //实体id
    private int entityId;
    //被点赞人，实体的用户id
    private int entityUserId;
    //额外的数据存入map
    private Map<String,Object> data = new HashMap<>();

    public void setData(Map<String, Object> data) {   
        this.data = data;
    }
    public Event setData(String key,String object){  //对map数据的链式set
        this.data.put(key,object);
        return this;
    }
}
