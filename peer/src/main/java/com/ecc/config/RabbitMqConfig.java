package com.ecc.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue queue1(){
        return new Queue("queue_1");
    }

    @Bean
    public Queue queue2(){
        return new Queue("queue_2");
    }
}
