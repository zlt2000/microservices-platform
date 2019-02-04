package com.central.oauth.filter;

import com.central.common.constant.SecurityConstants;
import com.central.oauth.exception.ValidateCodeException;
import com.central.oauth.service.IValidateCodeService;
import com.central.oauth2.common.properties.SecurityProperties;
import com.central.oauth2.common.util.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zlt
 * @date 2018/11/21
 */
@Slf4j
@Component("validateCodeFilter")
public class ValidateCodeFilter extends OncePerRequestFilter {
    @Autowired
    private IValidateCodeService validateCodeService;

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 验证码校验失败处理器
     */
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    /**
     * 验证请求url与配置的url是否匹配的工具类
     */
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 返回true代表不执行过滤器，false代表执行
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        //登录提交的时候验证验证码
        if (pathMatcher.match(SecurityConstants.PASSWORD_LOGIN_PRO_URL, request.getRequestURI())) {
            //判断是否有不验证验证码的client
            if (securityProperties.getCode().getIgnoreClientCode().length > 0) {
                try {
                    final String[] clientInfos = AuthUtils.extractClient(request);
                    String clientId = clientInfos[0];
                    for (String client : securityProperties.getCode().getIgnoreClientCode()) {
                        if (client.equals(clientId)) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    log.error("解析client信息失败", e);
                }
            }
            return false;
        }
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            validateCodeService.validate(request);
        } catch (ValidateCodeException e) {
            authenticationFailureHandler.onAuthenticationFailure(request, response, e);
            return;
        }
        chain.doFilter(request, response);
    }
}