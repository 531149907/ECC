package com.ecc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class OrdererServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrdererServiceApplication.class, args);
    }
}
