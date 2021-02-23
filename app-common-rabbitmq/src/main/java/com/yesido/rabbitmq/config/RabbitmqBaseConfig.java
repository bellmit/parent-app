package com.yesido.rabbitmq.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * rabbitmq配置类
 * 
 * @author yesido
 * @date 2019年7月25日 下午3:02:52
 */
@Configuration
@PropertySource(value = {"classpath:rabbitmq-${spring.profiles.active}.properties"},
        ignoreResourceNotFound = false)
public class RabbitmqBaseConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Bean(name = "connectionFactory")
    public ConnectionFactory connectionFactory() {

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, Integer.valueOf(port));
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        // 如果要进行消息发送确认回调，则这里必须要设置为true
        connectionFactory.setPublisherConfirms(true);
        // 如果要进行消息发送失败退回，则这里必须要设置为true
        connectionFactory.setPublisherReturns(true);
        // 该方法配置多个host，在当前连接host down掉的时候会自动去重连后面的host
        // connectionFactory.setAddresses("192.168.1.1:5672,192.168.1.2:5672");
        return connectionFactory;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置确认模式为手工确认
        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdminCommon(final ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.afterPropertiesSet();
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.setIgnoreDeclarationExceptions(true);
        return rabbitAdmin;
    }

}
