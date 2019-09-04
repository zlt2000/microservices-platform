package com.central.gateway.filter.pre;

import cn.hutool.core.util.StrUtil;
import com.central.common.constant.CommonConstant;
import com.central.common.constant.ConfigConstants;
import com.central.common.context.LbIsolationContextHolder;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * 保存负载均衡隔离值
 *
 * @author zlt
 * @date 2019/8/13
 */
@Component
public class LbIsolationFilter extends ZuulFilter {
    @Value("${" + ConfigConstants.CONFIG_RIBBON_ISOLATION_ENABLED + ":false}")
    private boolean enableIsolation;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //根据配置控制是否开启过滤器
        return enableIsolation;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String version = ctx.getRequest().getHeader(CommonConstant.Z_L_T_VERSION);
        if (StrUtil.isNotEmpty(version)) {
            LbIsolationContextHolder.setVersion(version);
        } else {
            LbIsolationContextHolder.clear();
        }
        return null;
    }
}
