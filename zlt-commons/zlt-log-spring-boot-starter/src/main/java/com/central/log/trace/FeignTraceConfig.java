package com.central.log.trace;

import com.central.log.properties.TraceProperties;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * feign拦截器，传递traceId
 *
 * @author zlt
 * @date 2021/1/28
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Configuration
@ConditionalOnClass(value = {RequestInterceptor.class})
public class FeignTraceConfig {
    @Resource
    private TraceProperties traceProperties;

    @Bean
    public RequestInterceptor feignTraceInterceptor() {
        return template -> {
            if (traceProperties.getEnable()) {
                //传递日志traceId
                String traceId = MDCTraceUtils.getTraceId();
                if (!StringUtils.isEmpty(traceId)) {
                    String spanId = MDCTraceUtils.getSpanId();
                    template.header(MDCTraceUtils.TRACE_ID_HEADER, traceId);
                    template.header(MDCTraceUtils.SPAN_ID_HEADER, spanId);
                }
            }
        };
    }
}
