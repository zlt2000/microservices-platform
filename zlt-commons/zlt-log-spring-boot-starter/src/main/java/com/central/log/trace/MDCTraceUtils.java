package com.central.log.trace;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.MDC;

import java.util.concurrent.atomic.AtomicInteger;

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
     * 块id的名称
     */
    public static final String KEY_SPAN_ID = "spanId";

    /**
     * 日志链路追踪id信息头
     */
    public static final String TRACE_ID_HEADER = "x-traceId-header";
    /**
     * 日志链路块id信息头
     */
    public static final String SPAN_ID_HEADER = "x-spanId-header";

    /**
     * filter的优先级，值越低越优先
     */
    public static final int FILTER_ORDER = -1;

    private static final TransmittableThreadLocal<AtomicInteger> spanNumber = new TransmittableThreadLocal<>();

    /**
     * 创建traceId并赋值MDC
     */
    public static void addTrace() {
        String traceId = createTraceId();
        MDC.put(KEY_TRACE_ID, traceId);
        MDC.put(KEY_SPAN_ID, "0");
        initSpanNumber();
    }

    /**
     * 赋值MDC
     */
    public static void putTrace(String traceId, String spanId) {
        MDC.put(KEY_TRACE_ID, traceId);
        MDC.put(KEY_SPAN_ID, spanId);
        initSpanNumber();
    }

    /**
     * 获取MDC中的traceId值
     */
    public static String getTraceId() {
        return MDC.get(KEY_TRACE_ID);
    }
    /**
     * 获取MDC中的spanId值
     */
    public static String getSpanId() {
        return MDC.get(KEY_SPAN_ID);
    }

    /**
     * 清除MDC的值
     */
    public static void removeTrace() {
        MDC.remove(KEY_TRACE_ID);
        MDC.remove(KEY_SPAN_ID);
        spanNumber.remove();
    }

    /**
     * 创建traceId
     */
    public static String createTraceId() {
        return IdUtil.getSnowflake().nextIdStr();
    }

    public static String getNextSpanId() {
        return StrUtil.format("{}.{}", getSpanId(), spanNumber.get().incrementAndGet());
    }

    private static void initSpanNumber() {
        spanNumber.set(new AtomicInteger(0));
    }
}
