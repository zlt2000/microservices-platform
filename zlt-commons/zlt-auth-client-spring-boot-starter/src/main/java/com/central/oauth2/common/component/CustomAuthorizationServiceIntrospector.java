package com.central.oauth2.common.component;

import com.central.common.constant.SecurityConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.security.Principal;
import java.util.Objects;

/**
 * 内省 token 解析器
 *
 * @author: zlt
 * @date: 2023/12/5
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationServiceIntrospector implements OpaqueTokenIntrospector {
    private final OAuth2AuthorizationService authorizationService;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (Objects.isNull(authorization)) {
            throw new InvalidBearerTokenException("invalid_token: " + token);
        }

        // 客户端模式默认返回
        if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(authorization.getAuthorizationGrantType())) {
            return new DefaultOAuth2AuthenticatedPrincipal(authorization.getPrincipalName()
                    , authorization.getAttributes(), AuthorityUtils.NO_AUTHORITIES);
        }

        String accountType = (String)authorization.getAttributes().get(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME);
        Authentication authentication = (Authentication)authorization.getAttributes().get(Principal.class.getName());
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal)authentication.getPrincipal();
        principal.getAttributes().put(SecurityConstants.CLIENT_ID, authorization.getRegisteredClientId());
        principal.getAttributes().put(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME, accountType);

        return principal;
    }
}
