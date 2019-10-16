package com.central.common.filter;

import cn.hutool.core.util.StrUtil;
import com.central.common.constant.CommonConstant;
import com.central.log.properties.TraceProperties;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 日志链路追踪过滤器
 *
 * @author zlt
 * @date 2019/9/15
 */
@ConditionalOnClass(Filter.class)
public class TraceFilter extends OncePerRequestFilter {
    @Resource
    private TraceProperties traceProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !traceProperties.getEnable();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        try {
            String traceId = request.getHeader(CommonConstant.TRACE_ID_HEADER);
            if (StrUtil.isNotEmpty(traceId)) {
                MDC.put(CommonConstant.LOG_TRACE_ID, traceId);
            }

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
