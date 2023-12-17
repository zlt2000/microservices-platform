package com.central.oauth.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

/**
 * @author: zlt
 * @date: 2023/11/16
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class CustomOAuth2AuthenticationException extends OAuth2AuthenticationException {
    public CustomOAuth2AuthenticationException(String msg) {
        super(new OAuth2Error(msg), msg);
    }
}
