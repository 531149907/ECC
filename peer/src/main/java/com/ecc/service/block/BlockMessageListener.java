package com.ecc.service.block;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BlockMessageListener {

    @RabbitListener(queues = "queue_1")
    public void process(String message) {
        System.out.println("received: " + message);
    }
}
