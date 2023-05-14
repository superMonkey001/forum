package cn.edu.hncj.forum.notify.producer;

import cn.edu.hncj.forum.notify.entity.Event;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component //声明为一个组件，每次都是自动检测装配为bean,每次调用都会产生一个新的实体
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;
    //处理事件，本质上是发送消息
    public void fireEvent(Event event){  //触发事件
        //将事件发送到指定的主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
