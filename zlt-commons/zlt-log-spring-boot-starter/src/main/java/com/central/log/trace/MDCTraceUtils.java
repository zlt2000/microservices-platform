package com.central.log.trace;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * 日志追踪工具类
 *
 * @author zlt
 * @date 2020/10/14
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class MDCTraceUtils {
    /**
     * 追踪id的名称
     */
    public static final String KEY_TRACE_ID = "traceId";

    /**
     * 日志链路追踪id信息头
     */
    public static final String TRACE_ID_HEADER = "x-traceId-header";

    /**
     * filter的优先级，值越低越优先
     */
    public static final int FILTER_ORDER = -1;

    /**
     * 创建traceId并赋值MDC
     */
    public static void addTraceId() {
        MDC.put(KEY_TRACE_ID, createTraceId());
    }

    /**
     * 赋值MDC
     */
    public static void putTraceId(String traceId) {
        MDC.put(KEY_TRACE_ID, traceId);
    }

    /**
     * 获取MDC中的traceId值
     */
    public static String getTraceId() {
        return MDC.get(KEY_TRACE_ID);
    }

    /**
     * 清除MDC的值
     */
    public static void removeTraceId() {
        MDC.remove(KEY_TRACE_ID);
    }

    /**
     * 创建traceId
     */
    public static String createTraceId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
