package com.yesido.zookeeper.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * ZooKeeper 配置
 * 
 * @author yesido
 * @date 2019年8月9日 下午3:59:22
 */
@Configuration
@PropertySource(value = {"zookeeper-${spring.profiles.active}.properties"},
        ignoreResourceNotFound = false)
public class ZooKeeperConfig {
    Logger logger = LoggerFactory.getLogger(getClass());

    // zookeeper服务器地址及端口号
    @Value("${zookeeper.server}")
    private String zookeeperServer;

    // 会话超时时间，单位毫秒
    @Value("${zookeeper.session.timeout:5000}")
    private int sessionTimeout;

    // 重试次数
    @Value("${zookeeper.retry_policy:5}")
    private int retryCount;

    @Bean
    public CuratorFramework curatorFramework() {
        // 重试策略，初试时间1秒，重试retryCount次 
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, retryCount);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zookeeperServer)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(retryPolicy)
                .build();
        client.start(); // 开始连接
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState state) {
                if (state == ConnectionState.LOST) {
                    // 连接丢失
                    logger.info("zk状态：lost session with zookeeper");
                } else if (state == ConnectionState.CONNECTED) {
                    // 连接新建
                    logger.info("zk状态：connected with zookeeper");
                } else if (state == ConnectionState.RECONNECTED) {
                    // 连接重连
                    logger.info("zk状态：reconnected with zookeeper");
                } else if (state == ConnectionState.SUSPENDED) {
                    // 连接挂起
                    logger.info("zk状态：suspended with zookeeper");
                } else if (state == ConnectionState.READ_ONLY) {
                    // 连接已经准备好
                    logger.info("zk状态：read only with zookeeper");
                }
            }
        });
        client.getCuratorListenable().addListener(new CuratorListener() {
            @Override
            public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
                logger.info("事件：{}", event);
            }
        });
        client.getUnhandledErrorListenable().addListener(new UnhandledErrorListener() {
            @Override
            public void unhandledError(String message, Throwable e) {
                logger.error("错误事件：{}", message, e);
            }
        });
        return client;
    }
}
