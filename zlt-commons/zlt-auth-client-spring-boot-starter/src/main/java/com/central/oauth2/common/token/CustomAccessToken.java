package com.central.oauth2.common.token;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import java.util.Map;

/**
 * @author: zlt
 * @date: 2023/11/20
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class CustomAccessToken extends OAuth2Authorization.Token<OAuth2AccessToken> {
    public CustomAccessToken(OAuth2AccessToken token, Map metadata) {
        super(token, metadata);
    }
}
