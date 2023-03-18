package cn.edu.hncj.forum.letter.entity;

import io.netty.channel.Channel;

import java.util.HashMap;

/**
 * @Author FanJian
 * @Date 2022-12-26 21:04
 */

public class UserChannelRel {
    private static HashMap<Long, Channel> manager = new HashMap<>();

    public static void put(Long senderId,Channel channel) {
        manager.put(senderId,channel);
    }
    public static Channel get(Long senderId){
        return manager.get(senderId);
    }

    public static void output() {
        // 输出所有的用户信息 {userId: channelId}
        for (HashMap.Entry<Long,Channel> entry : manager.entrySet()) {
            System.out.println("UserId:" + entry.getKey() + ", ChannelId" + entry.getValue().id().asLongText());
        }
    }
}
