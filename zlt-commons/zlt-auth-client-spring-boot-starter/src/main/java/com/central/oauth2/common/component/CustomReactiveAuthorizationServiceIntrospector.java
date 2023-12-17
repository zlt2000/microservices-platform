package com.central.oauth2.common.component;

import com.central.common.constant.SecurityConstants;
import com.central.oauth2.common.util.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.*;

/**
 * webFlux 内省 token 解析器
 *
 * @author zlt
 * @version 1.0
 * @date 2023/12/4
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
public class CustomReactiveAuthorizationServiceIntrospector implements ReactiveOpaqueTokenIntrospector {
    @Override
    public Mono<OAuth2AuthenticatedPrincipal> introspect(String accessTokenValue) {
        return Mono.just(accessTokenValue)
                .map(AuthUtils::checkAccessTokenToAuth)
                .map(this::convertClaimsSet)
                .onErrorMap((e) -> !(e instanceof OAuth2IntrospectionException), this::onError);
    }

    private OAuth2AuthenticatedPrincipal convertClaimsSet(OAuth2Authorization authorization) {
        Map<String, Object> claims = new HashMap<>();
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        claims.put(SecurityConstants.CLIENT_ID, authorization.getRegisteredClientId());
        claims.putAll(authorization.getAttributes());

        Authentication authentication = (Authentication)authorization.getAttributes().get(Principal.class.getName());
        if (authentication != null) {
            authorities.addAll(authentication.getAuthorities());
        }

        return new OAuth2IntrospectionAuthenticatedPrincipal(authorization.getPrincipalName(), claims, authorities);
    }

    private OAuth2IntrospectionException onError(Throwable ex) {
        return new OAuth2IntrospectionException(ex.getMessage(), ex);
    }
}
