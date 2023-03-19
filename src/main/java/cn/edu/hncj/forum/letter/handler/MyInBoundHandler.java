package cn.edu.hncj.forum.letter.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Author FanJian
 * @Date 2023-03-18 13:30
 */

public class MyInBoundHandler extends SimpleChannelInboundHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到新连接");
        //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast(new HttpServerCodec());
        //以块的方式来写的处理器
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(8192));
        //ctx.pipeline().addLast(new OnlineWebSocketHandler());//添加在线客服聊天消息处理类
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws", null, true, 65536 * 10));
        //添加测试的聊天消息处理类
        pipeline.addLast(new WebSocketHandler());
    }

}
