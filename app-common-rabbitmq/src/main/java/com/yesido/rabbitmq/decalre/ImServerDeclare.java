package com.yesido.rabbitmq.decalre;

/**
 * rabbitmq 常量定义 <br/>
 * 
 * <pre>
 * org.springframework.amqp.rabbit.annotation.Queue 参数说明：
 * value：队列名称
 * durable：队列是否持久化到数据库，true=表示持久化，服务崩溃也不会丢失队列，false=非持久化，服务重启队列丢失
 * autoDelete：设置为true的话若没有消费者订阅该队列，队列将被删除
 * </pre>
 * 
 * <pre>
 * void basicAck(long deliveryTag, boolean multiple)
 * deliveryTag：消息标识，对于每个Channel来说，每个消息都会有一个DeliveryTag，一般用接收消息的顺序来表示：1,2,3,4 等等，
 * 如deliveryTag=5时，再配合multiple=true，则deliveryTag小于5的也会做同样的操作
 * multiple：false只确认当前一个消息，true确认所有小于等于deliveryTag的未确认消息
 * </pre>
 * 
 * <pre>
 * void basicNack(long deliveryTag, boolean multiple, boolean requeue)
 * deliveryTag：同上
 * multiple：同上
 * requeue：是否重新入队，true=重新入队，false=抛弃消息
 * </pre>
 * 
 * <pre>
 * void basicReject(long deliveryTag, boolean requeue)：
 * deliveryTag：同上
 * requeue：同上
 * </pre>
 * 
 * @author yesido
 * @date 2019年7月19日 上午10:35:51
 */
public class ImServerDeclare {

    public static final String EX_FANOUT_IM_SERVER = "E.fanout.im.server";
    public static final String EX_TOPIC_IM_SERVER = "E.topic.im.server";

    /**
     * 路由消息队列
     */
    public static final String RK_IM_SERVER_MSG_ROUTE = "RK.im.server.msg.route"; // + IP
    public static final String Q_IM_SERVER_MSG_ROUTE = "Q.im.server.msg.route"; // + IP

    /**
     * 消息推送
     */
    public static final String RK_IM_SERVER_MSG_PUSH = "RK.im.server.msg.push";
    public static final String Q_IM_SERVER_MSG_PUSH = "Q.im.server.msg.push";
}
