package cn.edu.hncj.forum.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConfig {

    @Value("${zookeeper.connectString}")
    private String connectString;

    @Value("${zookeeper.sessionTimeoutMs}")
    private int sessionTimeoutMs;

    @Value("${zookeeper.connectionTimeoutMs}")
    private int connectionTimeoutMs;

    @Value("${zookeeper.retry.baseSleepTimeMs}")
    private int baseSleepTimeMs;

    @Value("${zookeeper.retry.maxRetries}")
    private int maxRetries;

    @Bean
    public CuratorFramework curatorFramework() {
        return CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(sessionTimeoutMs)
                .connectionTimeoutMs(connectionTimeoutMs)
                .retryPolicy(new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries))
                .build();
    }
}
