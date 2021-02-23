package com.yesido.kafka.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

/**
 * kafka服务
 * 
 * @author yesido
 * @date 2019年11月21日 上午9:41:17
 */
@Service
public class KafkaService {
    Logger LOGGER = LoggerFactory.getLogger(KafkaService.class);

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Autowired
    private AdminClient adminClient;

    /**
     * 开启监听Listener
     * 
     * @param listenerId 监听器id
     */
    public void startListener(String listenerId) {
        LOGGER.info("开启kafka监听：{}", listenerId);
        MessageListenerContainer listener = registry.getListenerContainer(listenerId);
        if (listener == null) {
            LOGGER.warn("开启kafka监听失败，找不到对应的listener：{}", listenerId);
            return;
        }
        if (!listener.isRunning()) {
            listener.start();
        }
        listener.resume();
    }

    /**
     * 暂停监听Listener
     * 
     * @param listenerId 监听器id
     */
    public void shutdownListener(String listenerId) {
        LOGGER.info("关闭kafka监听：{}", listenerId);
        MessageListenerContainer listener = registry.getListenerContainer(listenerId);
        if (listener == null) {
            LOGGER.warn("关闭kafka监听失败，找不到对应的listener：{}", listenerId);
            return;
        }
        listener.pause();
    }

    /**
     * 创建kafka topic：不会修改到已存在的topic
     * 
     * <pre>
     * 使用spring @bean 方式创建可以修改分区数，只能增加，不能减少分区
     * </pre>
     * 
     * @param topicName topic名称
     * @param numPartitions 分区数量
     * @param replicationFactor 分片数量
     */
    public void createTopic(String topicName, int numPartitions, int replicationFactor) {
        NewTopic topic = new NewTopic(topicName, numPartitions, (short) replicationFactor);
        adminClient.createTopics(Arrays.asList(topic));
    }

    /**
     * 删除消费组
     * 
     * @param groupId
     */
    public void deleteConsumerGroup(String groupId) {
        List<String> list = new ArrayList<String>();
        list.add(groupId);
        adminClient.deleteConsumerGroups(list);
    }

    /**
     * 删除消费组
     * 
     * @param groupIds
     */
    public void deleteConsumerGroups(List<String> groupIds) {
        adminClient.deleteConsumerGroups(groupIds);
    }
}
