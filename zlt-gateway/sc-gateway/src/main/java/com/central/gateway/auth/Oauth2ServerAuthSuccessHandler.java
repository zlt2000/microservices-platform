package com.central.gateway.auth;

import cn.hutool.core.collection.CollectionUtil;
import com.central.common.constant.SecurityConstants;
import com.central.common.model.LoginAppUser;
import com.central.common.utils.LoginUserUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;

/**
 * 认证成功处理类
 *
 * @author zlt
 * @date 2019/10/7
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Component
public class Oauth2ServerAuthSuccessHandler implements ServerAuthenticationSuccessHandler {
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        MultiValueMap<String, String> headerValues = new LinkedMultiValueMap<>(4);
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2IntrospectionAuthenticatedPrincipal authenticatedPrincipal) {
            Map<String, Object> claims = authenticatedPrincipal.getAttributes();
            Authentication auth = (Authentication)claims.get(Principal.class.getName());
            LoginAppUser user = LoginUserUtils.getUser(auth);
            if (user != null) {
                headerValues.add(SecurityConstants.USER_ID_HEADER, String.valueOf(user.getId()));
                headerValues.add(SecurityConstants.USER_HEADER, user.getUsername());
            }

            headerValues.add(SecurityConstants.ROLE_HEADER, CollectionUtil.join(authentication.getAuthorities(), ","));
            headerValues.add(SecurityConstants.TENANT_HEADER, (String)claims.get(SecurityConstants.CLIENT_ID));
            headerValues.add(SecurityConstants.ACCOUNT_TYPE_HEADER, (String)claims.get(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME));
        }

        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                .headers(h -> h.addAll(headerValues))
                .build();

        ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
        return webFilterExchange.getChain().filter(build);
    }
}
