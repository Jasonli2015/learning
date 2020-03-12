package com.jason.learning.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 入门程序的消费者(工作队列模式)
 * */
public class Consumer01_Worker {

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
        try {
            // 建立新连接
            connection = connectionFactory.newConnection();
            // 创建会话通道，生产者和mq服务所有通讯都在channel通道中完成
            Channel channel = connection.createChannel();

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

            // 实现消费方法
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
                /**
                 * 当接收到消息之后方法将别调用
                 * 参数明细：String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body
                 * @param consumerTag 消费者标签，用来标识消费者，监听队列时设置channel.basicConsumer
                 * @param envelope 信封，通过envelope
                 * @param properties 消息属性
                 * @param body 消息内容
                 * */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    // 交换机
                    String exchange = envelope.getExchange();
                    // 消息id：mq在channel中用来标志消费者的id，可用于确认消息已接收
                    long deliverTag = envelope.getDeliveryTag();
                    // 消息内容
                    String message = new String(body,"UTF-8");
                    System.out.println("receive message: " + message);
                }
            };

            /**
             * 监听队列
             *
             * 参数明细：String queue, boolean autoAck, Consumer callback
             * 1. queue 队列名称
             * 2. autoAck 自动回复，当消费者接收到消息后要告诉mq消息已接收，如果此参数设置为true表示会自动回复mq,如果设置为false要通过编程实现回复
             * 3. callback 消费方法，当消费者接收到消息要执行的方法
             * */
            channel.basicConsume(QUEUE, true, defaultConsumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
