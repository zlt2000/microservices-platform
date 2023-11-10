package com.rocketmq.demo.controller;

import cn.hutool.core.util.RandomUtil;
import com.central.common.utils.IdGenerator;
import com.rocketmq.demo.model.Order;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zlt
 */
@RestController
public class OrderController {
    private final static String ORDER_OUT = "order-out-0";
    private final StreamBridge streamBridge;

    public OrderController(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    /**
     * 正常情况
     */
    @GetMapping("/success")
    public String success() {
        Order order = new Order();
        order.setOrderId(IdGenerator.getId());
        order.setOrderNo(RandomUtil.randomString(4));

        Message<Order> message = MessageBuilder
                .withPayload(order)
                .setHeader("orderId", order.getOrderId())
                .build();
        //发送半消息
        streamBridge.send(ORDER_OUT, message);
        return "下单成功";
    }

    /**
     * 发送消息失败
     */
    @GetMapping("/produceError")
    public String produceError() {
        Order order = new Order();
        order.setOrderId(IdGenerator.getId());
        order.setOrderNo(RandomUtil.randomString(4));

        Message<Order> message = MessageBuilder
                .withPayload(order)
                .setHeader("orderId", order.getOrderId())
                .setHeader("produceError", "1")
                .build();
        //发送半消息
        streamBridge.send(ORDER_OUT, message);
        return "发送消息失败";
    }

    /**
     * 消费消息失败
     */
    @GetMapping("/consumeError")
    public String consumeError() {
        Order order = new Order();
        order.setOrderId(IdGenerator.getId());
        order.setOrderNo(RandomUtil.randomString(4));

        Message<Order> message = MessageBuilder
                .withPayload(order)
                .setHeader("orderId", order.getOrderId())
                .setHeader("consumeError", "1")
                .build();
        //发送半消息
        streamBridge.send(ORDER_OUT, message);
        return "消费消息失败";
    }
}
