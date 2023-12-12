package com.central.oauth.support.mobile;

import com.central.oauth.service.impl.UserDetailServiceFactory;
import com.central.oauth.support.base.BaseAuthenticationProvider;
import com.central.oauth.exception.CustomOAuth2AuthenticationException;
import com.central.oauth2.common.token.MobileAuthenticationToken;
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
 * @author zlt
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Setter
@Getter
public class MobileAuthenticationProvider extends BaseAuthenticationProvider {
    private UserDetailServiceFactory userDetailsServiceFactory;
    private PasswordEncoder passwordEncoder;

    public MobileAuthenticationProvider(OAuth2AuthorizationService authorizationService
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
        return MobileAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    protected Authentication getPrincipal(Authentication authentication) {
        MobileAuthenticationToken authToken = (MobileAuthenticationToken) authentication;
        String mobile = (String) authToken.getPrincipal();
        String password = authToken.getCredentials();
        UserDetails userDetails;
        try {
            userDetails = userDetailsServiceFactory.getService(authToken).loadUserByMobile(mobile);
        } catch (AuthenticationException e) {
            throw new CustomOAuth2AuthenticationException(e.getMessage());
        }
        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new CustomOAuth2AuthenticationException("手机号或密码错误");
        }
        return new MobileAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    protected AuthorizationGrantType grantType() {
        return MobileAuthenticationToken.GRANT_TYPE;
    }
}
