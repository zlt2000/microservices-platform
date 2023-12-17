package com.central.oauth2.common.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

/**
 * @author: zlt
 * @date: 2023/12/10
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class CustomOAuth2AuthorizationException extends OAuth2AuthenticationException {
    public CustomOAuth2AuthorizationException(String msg) {
        super(new OAuth2Error(msg), msg);
    }
}
