package com.central.oauth.filter;

import cn.hutool.core.util.ArrayUtil;
import com.central.common.constant.SecurityConstants;
import com.central.common.context.TenantContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 设置租户id过滤器
 *
 * @author zlt
 * @date 2020/3/29
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class LoginProcessSetTenantFilter extends OncePerRequestFilter {
    private static final String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    private RequestMatcher requiresAuthenticationRequestMatcher;
    public LoginProcessSetTenantFilter() {
        requiresAuthenticationRequestMatcher = new AntPathRequestMatcher(SecurityConstants.OAUTH_LOGIN_PRO_URL, HttpMethod.POST.name());
    }

    /**
     * 返回true代表不执行过滤器，false代表执行
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (requiresAuthentication(request)) {
            return false;
        }
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            DefaultSavedRequest savedRequest = (DefaultSavedRequest)request.getSession().getAttribute(SAVED_REQUEST);
            if (savedRequest != null) {
                String[] clientIds = savedRequest.getParameterValues("client_id");
                if (ArrayUtil.isNotEmpty(clientIds)) {
                    //保存租户id
                    TenantContextHolder.setTenant(clientIds[0]);
                }
            }
            chain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }
}
