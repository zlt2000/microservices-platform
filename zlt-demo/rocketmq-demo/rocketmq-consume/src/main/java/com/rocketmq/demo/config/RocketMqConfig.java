package com.rocketmq.demo.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import com.rocketmq.demo.config.RocketMqConfig.MySink;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author zlt
 */
@EnableBinding({MySink.class})
public class RocketMqConfig {
    public interface MySink {
        @Input(Sink.INPUT)
        SubscribableChannel input();

        @Input("input2")
        SubscribableChannel input2();

        @Input("input3")
        SubscribableChannel input3();
    }
}
