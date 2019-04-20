package com.rocketmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Rocketmq消费者 demo
 *
 * @author zlt
 */
@SpringBootApplication
public class RocketMqConsumeApplication {
    public static void main(String[] args) {
        SpringApplication.run(RocketMqConsumeApplication.class, args);
    }
}
