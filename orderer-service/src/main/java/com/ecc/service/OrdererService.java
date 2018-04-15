package com.ecc.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdererService {

    @Autowired
    AmqpTemplate template;

    public void send(String message) {
        System.out.println("sent: " + message);
        template.convertAndSend("queue_1",message);
    }
}
