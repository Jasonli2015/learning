package com.jason.learning.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 入门程序的生产者(通配符匹配模式)
 * */
public class Producer04_Topics {

    // 发送邮件队列
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    // 发送短信队列
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    // 交换机
    private static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";
    // 路由key
    private static final String ROUTING_EMAIL = "inform.#.email.#";
    private static final String ROUTING_SMS = "inform.#.sms.#";

    public static void main(String[] args) {
        // 通过连接工厂创建新的连接和mq连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("jason");
        connectionFactory.setPassword("123456");
        // 设置虚拟机，一个mq服务可以设置多个虚拟机，每个虚拟机就相当于一个独立的mq
        connectionFactory.setVirtualHost("/");

        Connection connection = null;
        Channel channel = null;
        try {
            // 建立新连接
            connection = connectionFactory.newConnection();
            // 创建会话通道，生产者和mq服务所有通讯都在channel通道中完成
            channel = connection.createChannel();

            /**
             * 声明队列
             *
             * 参数明细：String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
             * 1. queue 队列名称
             * 2. durable 是否持久化，如果持久，mq重启后队列还在
             * 3. exclusive 是否独占连接，队列只允许在改连接中访问，如果connection关闭队列自动删除，如果将此参数设置true可用于临时队列的创建
             * 4. autoDelete 自动删除，队列不在使用时是否自动删除此队列，如果将此参数和exclusive参数设置为true就可以实现临时队列（队列不用就自动删除）
             * 5. arguments 扩展参数，可以设置一个队列的扩展参数，比如：设置存活时间
             * */
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);

            /***
             * 声明交换机
             *
             * 参数明细：String exchange, String type
             * 1. exchange 交换机名称
             * 2. type 交换机类型：
             *  fanout: 对应 Pbulish/Subscribe 工作模式
             *  direct: 对应 Routing 工作模式
             *  topics: 对应 Topics 工作模式
             *  header: 对应 Headers 工作模式
             */
            channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM, BuiltinExchangeType.TOPIC);

            /**
             * 队列和交换机进行绑定
             *
             * 参数明细：String queue, String exchange, String routingKey
             * 1. queue 队列名称
             * 2. exchange 交换机名称
             * 3. routingKey 路由key，作用是交换机根据路由key的值将消息转发到指定队列中，在发布订阅模式中为空字符串
             * */
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_TOPICS_INFORM, ROUTING_EMAIL);
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_TOPICS_INFORM, ROUTING_SMS);

            // 消息
            String emailMessage = "Send email inform message to user ";
            String smsMessage = "Send sms inform message to user ";
            String message = "Send inform message to user ";

            for (int i = 0; i < 5; i++) {
                /**
                 * 发送消息
                 *
                 * 参数明细：
                 * 1. exchange 交换机，如果不指定将使用mq的默认交换机(设置为"")
                 * 2. routingKey 路由key，交换机根据路由key来将消息转发到指定队列，如果使用默认交换机，routingKey设置为队列名称
                 * 3. props 消息属性
                 * 4. boy 消息内容
                 * */
                channel.basicPublish(EXCHANGE_TOPICS_INFORM, "inform.email", null, emailMessage.getBytes());
                System.out.println("send message: " + emailMessage);
                channel.basicPublish(EXCHANGE_TOPICS_INFORM, "inform.sms", null, smsMessage.getBytes());
                System.out.println("send message: " + smsMessage);
                channel.basicPublish(EXCHANGE_TOPICS_INFORM, "inform.email.sms", null, message.getBytes());
                System.out.println("send message: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭通道
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            // 关闭连接
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
