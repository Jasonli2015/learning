package com.jason.learning.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

/**
 * 入门程序的生产者(工作队列模式：轮询发送消息给消费者)
 * */
public class Producer01_Worker {

    // 队列
    private static final String QUEUE = "MyQueue";

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
            channel.queueDeclare(QUEUE, true, false, false, null);

            // 消息
            String message = "Hello World!";

            /**
             * 发送消息
             *
             * 参数明细：
             * 1. exchange 交换机，如果不指定将使用mq的默认交换机(设置为"")
             * 2. routingKey 路由key，交换机根据路由key来将消息转发到指定队列，如果使用默认交换机，routingKey设置为队列名称
             * 3. props 消息属性
             * 4. boy 消息内容
             * */
            channel.basicPublish("", QUEUE, null, message.getBytes());
            System.out.println("send message: " + message);
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
