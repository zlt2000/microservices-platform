package com.rocketmq.demo.service.impl;

import com.rocketmq.demo.model.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

/**
 * 积分服务的消费者，接收到下单成功后增加积分
 *
 * @author zlt
 */
@Configuration
public class IntegralReceiveService {
    @Bean
    public Consumer<Message<Order>> receive() {
        return message -> {
            //模拟消费异常
            String consumeError = (String)message.getHeaders().get("consumeError");
            if ("1".equals(consumeError)) {
                System.err.println("============Exception：积分进程挂了，消费消息失败");
                //模拟插入订单后服务器挂了，没有commit事务消息
                throw new RuntimeException("积分服务器挂了");
            }

            System.out.println("============收到订单信息，增加积分:" + message);
        };
    }

    /**
     * 消费死信队列
     */
    @Bean
    public Consumer<Message<Order>> receiveDlq() {
        return message -> {
            String orderId = (String)message.getHeaders().get("orderId");
            System.err.println("============消费死信队列消息，记录日志并预警：" + orderId);
        };
    }
}
