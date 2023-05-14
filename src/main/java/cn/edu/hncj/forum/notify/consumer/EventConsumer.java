package cn.edu.hncj.forum.notify.consumer;

import cn.edu.hncj.forum.notify.constant.CommunityConstant;
import cn.edu.hncj.forum.notify.entity.Event;
import cn.edu.hncj.forum.notify.entity.Message;
import cn.edu.hncj.forum.notify.service.DiscussPostService;
import cn.edu.hncj.forum.notify.service.MessageService;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {
    //记日志
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    //消息最终是要往message表中插入数据
    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;




    //定时器
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @KafkaListener(topics = {TOPIC_COMMENT})   //订阅不同的主题评论、关注、点赞
    public void handleCommentMessage(ConsumerRecord record){
        if (record==null||record.value()==null){
            logger.error("消息的内容为空");
            return;
        }
        //json转换为对象，并指定类型
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event==null){
            logger.error("消息格式错误");
            return;
        }
        //发送站内通知，主要是构造message对象，复用message表
        Message message = new Message();
        //User表中id为1代表系统用户
        message.setFromId(1);
        message.setToId(event.getEntityUserId());
        //消息id改为存主题
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        message.setStatus(0);
        //content内容，用来存显示的一句话，存json字符串用于拼接
        Map<String,Object> content = new HashMap<>();
        //是谁触发的
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());

        if (!event.getData().isEmpty()){   //将其他数据也存入
            for (Map.Entry<String,Object> entry:
                 event.getData().entrySet()) {
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

}
