package cn.edu.hncj.forum.letter.handler;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @Author FanJian
 * @Date 2023-03-18 13:45
 */

public class ChannelInitializer extends io.netty.channel.ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel client) throws Exception {
        ChannelPipeline pipeline = client.pipeline();
        pipeline.addLast(new MyInBoundHandler());
    }
}
