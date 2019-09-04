package com.central.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.central.common.constant.CommonConstant;
import com.central.common.constant.SecurityConstants;
import com.central.common.context.TenantContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 租户拦截器
 *
 * @author zlt
 * @date 2019/8/5
 */
public class TenantInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //优先获取请求参数中的tenantId值
        String tenantId = request.getParameter(CommonConstant.TENANT_ID_PARAM);
        if (StrUtil.isEmpty(tenantId)) {
            tenantId = request.getHeader(SecurityConstants.TENANT_HEADER);
        }
        //保存租户id
        if(StrUtil.isNotEmpty(tenantId)){
            TenantContextHolder.setTenant(tenantId);
        }
        return true;
    }
}
