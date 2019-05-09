package com.rocketmq;

import com.rocketmq.demo.model.Order;
import com.rocketmq.demo.service.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Rocketmq生产者 demo
 *
 * @author zlt
 */
@SpringBootApplication
public class RocketMqProduceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RocketMqProduceApplication.class, args);
    }

    @Bean
    public CustomRunner customRunner() {
        return new CustomRunner();
    }

    /**
     * 工程启动后执行
     * 共发送5条消息：2条为字符消息，3条为带tag的对象消息
     */
    public static class CustomRunner implements CommandLineRunner {
        @Autowired
        private SenderService senderService;

        @Override
        public void run(String... args) {
            int count = 5;
            for (int index = 1; index <= count; index++) {
                String msgContent = "msg-" + index;
                if (index % 2 == 0) {
                    senderService.send(msgContent);
                } else {
                    senderService.sendWithTags(new Order((long)index, "order-"+index), "tagObj");
                }
            }
        }
    }
}
