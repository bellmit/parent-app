package com.yesido.rabbitmq.sender;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@Component
public class RabbitSender implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        logger.error("消息发送失败，replyCode:{}, replyText:{}，exchange:{}，routingKey:{}，消息体:{}", replyCode, replyText, exchange, routingKey,
                new String(message.getBody()));
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            // logger.info("消息发送成功,消息：{}", correlationData);
        } else {
            logger.error("消息发送失败，消息：{}", correlationData);
        }
    }

    /**
     * 发送MQ消息 <br>
     * 消息是否发送成功用ConfirmCallback和ReturnCallback回调函数类确认
     * 
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, toString(message));
    }

    public void sendMessage(String routingKey, Object message) {
        rabbitTemplate.convertAndSend(routingKey, toString(message));
    }

    private String toString(Object value) {
        String _value = null;
        if (value instanceof String) {
            _value = value.toString();
        } else {
            _value = JSONObject.toJSONString(value);
        }
        return _value;
    }
}
