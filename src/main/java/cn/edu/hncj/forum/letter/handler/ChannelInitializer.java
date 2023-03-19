package cn.edu.hncj.forum.letter.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Author FanJian
 * @Date 2023-03-18 13:45
 */

@ChannelHandler.Sharable
public class ChannelInitializer extends io.netty.channel.ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel client) throws Exception {
        // ChannelPipeline pipeline = client.pipeline();
        // pipeline.addLast(new MyInBoundHandler());

        ChannelPipeline pipeline = client.pipeline();
        System.out.println("收到新连接");
        //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
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
