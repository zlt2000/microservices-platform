package com.central.common.filter;

import com.central.common.context.LoginUserContextHolder;
import com.central.common.model.SysUser;
import com.central.common.utils.LoginUserUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 获取当前登录人过滤器
 *
 * @author zlt
 * @date 2022/6/26
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@ConditionalOnClass(Filter.class)
@Order
public class LoginUserFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        try {
            SysUser user = LoginUserUtils.getCurrentUser(request, false);
            LoginUserContextHolder.setUser(user);
            filterChain.doFilter(request, response);
        } finally {
            LoginUserContextHolder.clear();
        }
    }
}
