package com.central.oauth.support.password;

import com.central.oauth.service.impl.UserDetailServiceFactory;
import com.central.oauth.support.base.BaseAuthenticationProvider;
import com.central.oauth.exception.CustomOAuth2AuthenticationException;
import com.central.oauth2.common.token.PasswordAuthenticationToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;

/**
 * 用户名密码 provider
 *
 * @author zlt
 * @version 1.0
 * @date 2023/11/18
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Setter
@Getter
public class PasswordAuthenticationProvider extends BaseAuthenticationProvider {
    private final UserDetailServiceFactory userDetailsServiceFactory;
    private final PasswordEncoder passwordEncoder;

    public PasswordAuthenticationProvider(OAuth2AuthorizationService authorizationService
            , OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator
            , UserDetailServiceFactory userDetailsServiceFactory, PasswordEncoder passwordEncoder) {
        super(authorizationService, tokenGenerator);
        Assert.notNull(userDetailsServiceFactory, "userDetailsServiceFactory cannot be null");
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.userDetailsServiceFactory = userDetailsServiceFactory;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    protected Authentication getPrincipal(Authentication authentication) {
        PasswordAuthenticationToken authToken = (PasswordAuthenticationToken) authentication;
        String username = (String) authToken.getPrincipal();
        String password = authToken.getCredentials();
        UserDetails userDetails;
        try {
            userDetails = userDetailsServiceFactory.getService(authToken).loadUserByUsername(username);
        } catch (AuthenticationException e) {
            throw new CustomOAuth2AuthenticationException(e.getMessage());
        }
        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new CustomOAuth2AuthenticationException("用户名或密码错误");
        }
        return new PasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    protected AuthorizationGrantType grantType() {
        return PasswordAuthenticationToken.GRANT_TYPE;
    }
}
