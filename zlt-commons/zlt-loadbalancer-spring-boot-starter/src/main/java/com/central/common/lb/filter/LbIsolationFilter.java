package com.central.common.lb.filter;

import cn.hutool.core.util.StrUtil;
import com.central.common.constant.CommonConstant;
import com.central.common.constant.ConfigConstants;
import com.central.common.context.LbIsolationContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * web过滤器
 * 作用：将请求中的存放在参数和header的版本号获取出来并存入TreadLocal中
 *
 * @author jarvis create by 2022/3/9
 */
@ConditionalOnClass(Filter.class)
public class LbIsolationFilter extends OncePerRequestFilter {
    @Value("${" + ConfigConstants.CONFIG_LOADBALANCE_ISOLATION_ENABLE + ":false}")
    private boolean enableIsolation;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !enableIsolation;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String version = StringUtils.isNotBlank(request.getParameter(CommonConstant.Z_L_T_VERSION))?
                    request.getParameter(CommonConstant.Z_L_T_VERSION):
                    request.getHeader(CommonConstant.Z_L_T_VERSION);
            if(StrUtil.isNotEmpty(version)){
                LbIsolationContextHolder.setVersion(version);
            }

            filterChain.doFilter(request, response);
        } finally {
            LbIsolationContextHolder.clear();
        }
    }
}
