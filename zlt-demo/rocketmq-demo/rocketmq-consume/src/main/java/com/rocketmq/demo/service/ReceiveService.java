package com.rocketmq.demo.service;

import com.rocketmq.demo.model.Order;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * @author zlt
 */
@Service
public class ReceiveService {
	/**
	 * 字符串消息
	 */
	@StreamListener(Sink.INPUT)
	public void receiveInput(String receiveMsg) {
		System.out.println("input receive: " + receiveMsg);
	}

	/**
	 * 对象消息
	 */
	@StreamListener("input2")
	public void receiveInput2(@Payload Order order) {
		System.out.println("input2 receive: " + order);
	}

	/**
	 * 通过spring.messaging对象来接收消息
	 */
    @StreamListener("input3")
    public void receiveInput3(Message msg) {
        System.out.println("input3 receive: " + msg);
    }
}