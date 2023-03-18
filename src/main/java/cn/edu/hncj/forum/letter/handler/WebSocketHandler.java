package cn.edu.hncj.forum.letter.handler;

import cn.edu.hncj.forum.letter.entity.ChatMsg;
import cn.edu.hncj.forum.letter.entity.DataContent;
import cn.edu.hncj.forum.letter.entity.UserChannelRel;
import cn.edu.hncj.forum.model.ChatList;
import cn.edu.hncj.forum.service.ChatListService;
import cn.edu.hncj.forum.service.UserLinkService;
import cn.edu.hncj.forum.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.hncj.forum.enums.MsgActionEnum.*;

/**
 * @Author FanJian
 * @Date 2023-03-18 13:47
 */

public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    Logger logger = LoggerFactory.getLogger(DefaultChannelGroup.class);

    @Autowired
    private UserLinkService userLinkService;

    @Autowired
    private ChatListService chatListService;

    private static ChannelGroup users =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取客户端传输过来的消息
        String content = msg.text();
        Channel currentChannel = ctx.channel();

        // 1. 获取客户端发的消息
        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        Integer action = dataContent.getAction();


        /// 2. 判断消息的类型
        // 2.1 如果是客户端进行连接
        if (action.equals(CONNECT.type)) {
            // =====================================================================
            // 1）当websocket第一次open的时候，初始化channel，把用户的channel和userid关联起来
            Long sendUserId = dataContent.getChatMsg().getSenderId();
            UserChannelRel.put(sendUserId, currentChannel);

            // 2）遍历所有的用户
            for (Channel c : users) {
                // 输出用户的所有channelId
                System.out.println(c.id().asLongText());
            }
            UserChannelRel.output();
            // ======================================================================


            Long fromId = dataContent.getChatMsg().getSenderId();
            Long toId = dataContent.getChatMsg().getReceiverId();
            Long min = Math.min(fromId, toId);
            Long max = Math.max(fromId, toId);
            // select linkId from user_link where `fromId` = #{min} and `toId` = #{max};
            // 为什么是上面这个sql呢？
            // 因为插入user_link的时候也是按照这个顺序插入的，其实所谓的fromId和toId是双方中的哪一个都无所谓，因为目的只是为了查出来所有的聊天记录信息或linkId
            Long linkId = userLinkService.selectLinkId(min, max);
            if (linkId == null) {
                userLinkService.insertLink(min,max);
            } else {
                // 通过linkId查找两者之间所有的聊天记录
                List<ChatList> chatLists = chatListService.selectChat(linkId);
                List<ChatMsg> messages = new ArrayList<>();
                for (ChatList chatList : chatLists) {
                    ChatMsg message = new ChatMsg();
                    message.setMsg(chatList.getContent());
                    // 如果chatList中的userId和当前登录的用户的id相等，那么说明，这条消息的发送方是当前用户
                    // 反之，发送方是对方
                    message.setSenderId(fromId.equals(chatList.getUserId()) ? fromId : toId);
                    message.setReceiverId(fromId.equals(chatList.getUserId()) ? toId : fromId);
                    messages.add(message);
                }
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(messages)));
            }

        }
        // 2.2 如果是发送私信
        else if (action.equals(CHAT.type)) {

            // 1)获取消息内容
            ChatMsg chatMsg = dataContent.getChatMsg();
            String msgText = chatMsg.getMsg();
            // 2)获取消息发送者
            Long fromId = dataContent.getChatMsg().getSenderId();
            // 3)获取消息接收者
            Long toId = dataContent.getChatMsg().getReceiverId();
            // 4)将消息持久化到数据库

            Long min = Math.min(fromId, toId);
            Long max = Math.max(fromId, toId);
            // select linkId from user_link where `fromId` = #{min} and `toId` = #{max};
            // 为什么是上面这个sql呢？
            // 因为插入user_link的时候也是按照这个顺序插入的，其实所谓的fromId和toId是双方中的哪一个都无所谓，因为目的只是为了查出来所有的聊天记录信息或linkId
            Long linkId = userLinkService.selectLinkId(min, max);
            if (linkId == null || linkId == 0) {
                 userLinkService.insertLink(min,max);
                 linkId = userLinkService.selectLinkId(min,max);
            }

            chatListService.insertChat(linkId,fromId, msgText, new Timestamp(System.currentTimeMillis()));

            // 判断是否在线, 给在线的用户返回响应, 刷新聊天框
            // 5)将消息发送给接收方
            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMsg(chatMsg);
            // 5.1)获取接收方的channel
            Channel channel = UserChannelRel.get(toId);
            // 5.2)如果用户离线，那就推送离线消息
            // 如果这里channel为空，说明用户连一次都没进入过websocket连接
            if (channel != null) {
                Channel userChannel = users.find(channel.id());
                // 5.3)
                // 如果这里userChannel为空，说明当前用户没有登录
                if (userChannel != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContentMsg)));
                }
            }
        }
        // 2.3 如果是签收消息
        else if (action.equals(SIGNED.type)) {

        }
        // 2.4 如果是保持长连接
        else if (action.equals(KEEPALIVE.type)) {

        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        users.remove(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String s = ctx.channel().id().asShortText();
        // 这个方法执行会自动删除对应的channel
        users.remove(ctx.channel());

    }


}