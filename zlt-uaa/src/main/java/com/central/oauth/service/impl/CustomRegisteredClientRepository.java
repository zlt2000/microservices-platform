package com.central.oauth.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.central.oauth2.common.pojo.ClientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author: zlt
 * @date: 2023/08/04
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@RequiredArgsConstructor
@Component
public class CustomRegisteredClientRepository implements RegisteredClientRepository {
    private final RegisteredClientService clientService;
    @Override
    public void save(RegisteredClient registeredClient) {

    }

    @Override
    public RegisteredClient findById(String id) {
        return this.findByClientId(id);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        ClientDto clientObj = clientService.loadClientByClientId(clientId);
        if (clientObj == null) {
            return null;
        }
        RegisteredClient.Builder builder = RegisteredClient.withId(clientObj.getClientId())
                .clientId(clientObj.getClientId())
                .clientSecret(clientObj.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

        if (StrUtil.isNotBlank(clientObj.getAuthorizedGrantTypes())) {
            for (String authorizedGrantType : clientObj.getAuthorizedGrantTypes().split(StrUtil.COMMA)) {
                builder.authorizationGrantType(new AuthorizationGrantType(authorizedGrantType));
            }
        }

        if (StrUtil.isNotBlank(clientObj.getWebServerRedirectUri())) {
            for (String redirectUri : clientObj.getWebServerRedirectUri().split(StrUtil.COMMA)) {
                builder.redirectUri(redirectUri);
            }
        }

        if (StrUtil.isNotBlank(clientObj.getScope())) {
            for (String scope : clientObj.getScope().split(StrUtil.COMMA)) {
                builder.scope(scope);
            }
        }

        OAuth2TokenFormat tokenFormat;
        if (OAuth2TokenFormat.SELF_CONTAINED.getValue().equals(clientObj.getTokenFormat())) {
            tokenFormat = OAuth2TokenFormat.SELF_CONTAINED;
        } else {
            tokenFormat = OAuth2TokenFormat.REFERENCE;
        }

        return builder
                .tokenSettings(
                        TokenSettings.builder()
                            .accessTokenFormat(tokenFormat)
                            .accessTokenTimeToLive(Duration.ofSeconds(clientObj.getAccessTokenValiditySeconds()))
                            .refreshTokenTimeToLive(Duration.ofSeconds(clientObj.getRefreshTokenValiditySeconds()))
                            .build()
                )
                .clientSettings(
                        ClientSettings.builder()
                            .requireAuthorizationConsent(!BooleanUtil.toBoolean(clientObj.getAutoapprove()))
                            .build()
                )
                .build();
    }
}
