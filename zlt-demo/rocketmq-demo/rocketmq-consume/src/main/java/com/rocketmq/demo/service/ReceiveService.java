package com.rocketmq.demo.service;

import com.rocketmq.demo.model.Order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

/**
 * @author zlt
 */
@Configuration
public class ReceiveService {
	/**
	 * 字符串消息
	 */
	@Bean
	public Consumer<String> receiveInput() {
		return receiveMsg -> System.out.println("input receive: " + receiveMsg);
	}

	/**
	 * 对象消息
	 */
	@Bean
	public Consumer<Order> receiveInput2() {
		return order -> System.out.println("input2 receive: " + order);
	}

	/**
	 * 通过spring.messaging对象来接收消息
	 */
	@Bean
	public Consumer<Message<String>> receiveInput3() {
		return msg -> System.out.println("input3 receive: " + msg);
	}
}