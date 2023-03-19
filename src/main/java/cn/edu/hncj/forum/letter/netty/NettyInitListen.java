package cn.edu.hncj.forum.letter.netty;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class NettyInitListen implements CommandLineRunner {

    // @Value("${netty.port}")
    // private Integer nettyPort;
    @Value("${server.port}")
    private Integer serverPort;

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("nettyServer starting ...");
            System.out.println("http://127.0.0.1:" + serverPort + "/chat");
            new NettyStarter(11111).start();
        } catch (Exception e) {
            System.out.println("NettyServerError:" + e);
        }
    }
}
