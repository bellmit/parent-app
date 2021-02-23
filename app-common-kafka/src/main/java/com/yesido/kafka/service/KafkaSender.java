package com.yesido.kafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.alibaba.fastjson.JSONObject;

/**
 * kafka发送
 * 
 * @author yesido
 * @date 2019年11月20日 下午3:12:53
 */
@Component
public class KafkaSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSender.class);

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    public ListenableFutureCallback<SendResult<Integer, String>> callbackSend = new ListenableFutureCallback<SendResult<Integer, String>>() {
        @Override
        public void onFailure(Throwable throwable) {
            LOGGER.error("发送kafka队列消息失败", throwable);
        }

        @Override
        public void onSuccess(SendResult<Integer, String> result) {
            LOGGER.info("发送kafka队列消息：{} -> {}", result.getProducerRecord().topic(), result.getProducerRecord().value());
        }
    };

    public void send(String topic, String msg) {
        /*ListenableFuture<SendResult<Integer, String>> future = kafkaTemplate.send(topic, msg);
        future.addCallback(callbackSend);*/
        kafkaTemplate.send(topic, msg);
    }

    public void send(String topic, Object object) {
        String msg = JSONObject.toJSONString(object);
        send(topic, msg);
    }
}
