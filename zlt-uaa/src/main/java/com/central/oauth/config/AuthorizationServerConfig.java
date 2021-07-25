package com.central.oauth.config;

import com.central.common.constant.SecurityConstants;
import com.central.common.model.SysUser;
import com.central.oauth.model.Client;
import com.central.oauth.service.IClientService;
import com.central.oauth.service.impl.RedisClientDetailsService;
import com.central.oauth.service.impl.UserDetailServiceFactory;
import com.central.oauth.utils.OidcIdTokenBuilder;
import com.central.oauth2.common.constants.IdTokenClaimNames;
import com.central.oauth2.common.properties.TokenStoreProperties;
import com.central.oauth2.common.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * OAuth2 授权服务器配置
 *
 * @author zlt
 * @date 2018/10/24
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Configuration
@EnableAuthorizationServer
@AutoConfigureAfter(AuthorizationServerEndpointsConfigurer.class)
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    /**
     * 注入authenticationManager 来支持 password grant type
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    @Resource
    private UserDetailServiceFactory userDetailsServiceFactory;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private WebResponseExceptionTranslator webResponseExceptionTranslator;

    @Autowired
    private RedisClientDetailsService clientDetailsService;

    @Autowired
    private RandomValueAuthorizationCodeServices authorizationCodeServices;

    @Autowired
    private TokenGranter tokenGranter;

    /**
     * 配置身份认证器，配置认证方式，TokenStore，TokenGranter，OAuth2RequestFactory
     * @param endpoints
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                //.userDetailsService(userDetailsServiceFactory.getService(SecurityConstants.DEF_ACCOUNT_TYPE))
                .authorizationCodeServices(authorizationCodeServices)
                .exceptionTranslator(webResponseExceptionTranslator)
                .tokenGranter(tokenGranter);
    }

    /**
     * 配置应用名称 应用id
     * 配置OAuth2的客户端相关信息
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
        clientDetailsService.loadAllClientToCache();
    }

    /**
     * 对应于配置AuthorizationServer安全认证的相关信息，创建ClientCredentialsTokenEndpointFilter核心过滤器
     * @param security
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security
                .tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("permitAll()")
                //让/oauth/token支持client_id以及client_secret作登录认证
                .allowFormAuthenticationForClients();
    }

    @Bean
    @Order(1)
    public TokenEnhancer tokenEnhancer(@Autowired(required = false) KeyProperties keyProperties
                , IClientService clientService
                , TokenStoreProperties tokenStoreProperties) {
        return (accessToken, authentication) -> {
            Set<String> responseTypes = authentication.getOAuth2Request().getResponseTypes();
            Map<String, Object> additionalInfo = new HashMap<>(3);
            String accountType = AuthUtils.getAccountType(authentication.getUserAuthentication());
            additionalInfo.put(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME, accountType);

            if (responseTypes.contains(SecurityConstants.ID_TOKEN)
                    || "authJwt".equals(tokenStoreProperties.getType())) {
                Object principal = authentication.getPrincipal();
                //增加id参数
                if (principal instanceof SysUser) {
                    SysUser user = (SysUser)principal;
                    if (responseTypes.contains(SecurityConstants.ID_TOKEN)) {
                        //生成id_token
                        setIdToken(additionalInfo, authentication, keyProperties, clientService, user);
                    }
                    if ("authJwt".equals(tokenStoreProperties.getType())) {
                        additionalInfo.put("id", user.getId());
                    }
                }
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        };
    }

    /**
     * 生成id_token
     * @param additionalInfo 存储token附加信息对象
     * @param authentication 授权对象
     * @param keyProperties 密钥
     * @param clientService 应用service
     */
    private void setIdToken(Map<String, Object> additionalInfo, OAuth2Authentication authentication
            , KeyProperties keyProperties, IClientService clientService, SysUser user) {
        String clientId = authentication.getOAuth2Request().getClientId();
        Client client = clientService.loadClientByClientId(clientId);
        if (client.getSupportIdToken()) {
            String nonce = authentication.getOAuth2Request().getRequestParameters().get(IdTokenClaimNames.NONCE);
            long now = System.currentTimeMillis();
            long expiresAt = System.currentTimeMillis() + client.getIdTokenValiditySeconds() * 1000;
            String idToken = OidcIdTokenBuilder.builder(keyProperties)
                    .issuer(SecurityConstants.ISS)
                    .issuedAt(now)
                    .expiresAt(expiresAt)
                    .subject(String.valueOf(user.getId()))
                    .name(user.getNickname())
                    .loginName(user.getUsername())
                    .picture(user.getHeadImgUrl())
                    .audience(clientId)
                    .nonce(nonce)
                    .build();

            additionalInfo.put(SecurityConstants.ID_TOKEN, idToken);
        }
    }
}
