package com.yesido.kafka.listener;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

/**
 * KafkaTemplate发送结果listener
 * 
 * @author yesido
 * @date 2019年11月20日 下午6:01:49
 */
@Component
public class KafkaSendResultListener implements ProducerListener<Integer, String> {
    Logger LOGGER = LoggerFactory.getLogger(KafkaSendResultListener.class);

    @Override
    public void onSuccess(ProducerRecord<Integer, String> producerRecord, RecordMetadata recordMetadata) {
        LOGGER.info("发送kafka队列消息：{} -> {}", producerRecord.topic(), producerRecord.value());
    }

    @Override
    public void onError(ProducerRecord<Integer, String> producerRecord, Exception e) {
        LOGGER.info("发送kafka队列消息失败：{} -> {}", producerRecord.topic(), producerRecord.value(), e);
    }
}
