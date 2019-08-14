package com.central.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.central.common.constant.CommonConstant;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志链路追踪拦截器
 *
 * @author zlt
 * @date 2019/8/13
 */
public class TraceInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(CommonConstant.TRACE_ID_HEADER);
        if (StrUtil.isNotEmpty(traceId)) {
            MDC.put(CommonConstant.LOG_TRACE_ID, traceId);
        }
        return true;
    }
}
