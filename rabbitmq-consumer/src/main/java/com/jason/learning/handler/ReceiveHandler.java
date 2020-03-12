package com.jason.learning.handler;

import com.jason.learning.config.RabbitmqConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReceiveHandler {

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public void sendEmail(String msg, Message message, Channel channel) {
        System.out.println("Receive message is: " + msg);
    }

}
