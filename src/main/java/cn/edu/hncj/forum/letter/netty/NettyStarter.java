package cn.edu.hncj.forum.letter.netty;

import cn.edu.hncj.forum.letter.handler.ChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * Netty服务器的启动器
 *
 * @Author FanJian
 * @Date 2023-03-18 12:40
 */

public class NettyStarter {


    private final int port;
    public NettyStarter(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            ChannelFuture channelFuture = bootstrap
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .group(boss, worker)
                    .localAddress(this.port)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer())
                    .bind()
                    .sync();
                    //.bind(new InetSocketAddress("127.0.0.1", port))

            System.out.println(NettyStarter.class + " 启动正在监听： " + channelFuture.channel().localAddress());
            // 关闭服务器通道
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 释放线程池资源
            worker.shutdownGracefully().sync();
            boss.shutdownGracefully().sync();
        }
    }


}
