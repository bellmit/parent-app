package com.yesido.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.messaging.Message;

import com.yesido.kafka.listener.KafkaSendResultListener;

/**
 * kafka配置
 * 
 * @author yesido
 * @date 2019年11月20日 下午2:39:00
 */
@Configuration
@PropertySource(value = {"classpath:kafka-${spring.profiles.active}.properties"},
        ignoreResourceNotFound = false)
public class KafkaConfig {
    Logger LOGGER = LoggerFactory.getLogger(KafkaConfig.class);
    // 消费群组
    public final static String GROUP_ID = "kafka-default-gid";

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${kafka.bootstrap.servers}")
    private String servers;
    @Value("${kafka.group.id:null}")
    private String groupId;

    @Value("${kafka.listener.ack-mode:MANUAL_IMMEDIATE}")
    private String ackMode;
    @Value("${kafka.listener.concurrency:5}")
    private int concurrency;

    @Value("${kafka.consumer.enable-auto-commit:false}")
    private boolean enableAutoCommit;

    @Value("${kafka.producer.retries:1}")
    private String retries;

    @Autowired
    private KafkaSendResultListener listener;

    public String getGroupId() {
        if (StringUtils.isBlank(groupId)) {
            groupId = GROUP_ID;
        }
        if ("prod".equals(profile)) {
            return groupId;
        }
        return groupId + "-" + profile;
    }

    /**
     * 消息过滤器
     */
    RecordFilterStrategy<Integer, String> recordFilter = new RecordFilterStrategy<Integer, String>() {
        public boolean filter(ConsumerRecord<Integer, String> consumerRecord) {
            // 返回true将会被丢弃
            if (consumerRecord == null || StringUtils.isBlank(consumerRecord.value())) {
                LOGGER.warn("kafka队列消息丢弃：{}", consumerRecord.toString());
                return true;
            }
            return false;
        }
    };

    /**
     * 创建Kafka监听器的工程类
     */
    @Bean
    @Primary
    public ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setAckMode(AckMode.valueOf(ackMode));
        factory.setAckDiscarded(true);
        factory.setRecordFilterStrategy(recordFilter);
        return factory;
    }

    /**
     * 使用此监听工厂类，KafkaListener不会自动启动，需要手动启动
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, String> delayContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        //禁止自动启动
        factory.setAutoStartup(false);
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setAckMode(AckMode.valueOf(ackMode));
        return factory;
    }

    /**
     * 消费者工厂
     */
    @Bean(name = "consumerFactory")
    public ConsumerFactory<Integer, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        // 连接地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        // GroupID
        props.put(ConsumerConfig.GROUP_ID_CONFIG, getGroupId());
        // 是否自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);
        // props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 200);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * 生产者工厂
     */
    @Bean
    public ProducerFactory<Integer, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        // 连接地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        // 重试，0为不启用重试机制
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * kafka操作对象kafkaTemplate
     */
    @Bean
    public KafkaTemplate<Integer, String> kafkaTemplate() {
        KafkaTemplate<Integer, String> kafkaTemplate = new KafkaTemplate<Integer, String>(producerFactory());
        kafkaTemplate.setProducerListener(listener);
        return kafkaTemplate;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> props = new HashMap<>();
        // 连接地址
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        KafkaAdmin admin = new KafkaAdmin(props);
        return admin;
    }

    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(kafkaAdmin().getConfig());
    }

    /**
     * Listener异常处理器
     */
    @Bean
    public ConsumerAwareListenerErrorHandler consumerAwareErrorHandler() {
        return new ConsumerAwareListenerErrorHandler() {
            @Override
            public Object handleError(Message<?> message, ListenerExecutionFailedException e, Consumer<?, ?> consumer) {
                LOGGER.error("kafka异常处理器：{} -> {}", message.getHeaders().get("kafka_receivedTopic"), message.getPayload(), e);
                consumer.commitAsync(); // ack提交数据
                return null;
            }
        };
    }
}
