package com.rocketmq.demo.service;

import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import javax.annotation.Resource;

/**
 * @author zlt
 */
@Service
public class SenderService {
	private final static String TEST_OUT = "test-out-0";

	@Resource
	private StreamBridge streamBridge;

	/**
	 * 发送字符消息
	 */
	public void send(String msg) {
		streamBridge.send(TEST_OUT, MessageBuilder.withPayload(msg).build());
	}

	/**
	 * 发送带tag的对象消息
	 */
	public <T> void sendWithTags(T msg, String tag) {
		Message<T> message = MessageBuilder.withPayload(msg)
				.setHeader(MessageConst.PROPERTY_TAGS, tag)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
				.build();
		streamBridge.send(TEST_OUT, message);
	}
}