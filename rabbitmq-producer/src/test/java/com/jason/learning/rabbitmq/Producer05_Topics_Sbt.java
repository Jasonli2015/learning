package com.jason.learning.rabbitmq;

import com.jason.learning.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 入门程序的生产者(通配符匹配模式-SpringBoot集成RabbitMQ)
 * */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Producer05_Topics_Sbt {

    @Autowired
    RabbitTemplate rabbitTemplate;

    String message = "Send email message to user";

    /**
     * 使用RabbitTemplate发送消息
     * 参数：
     * 1. 交换机名称
     * 2. routingKey
     * 3. 消息内容
     */
    @Test
    public void testSendEmail() {
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM, "inform.email", message);
    }
}
