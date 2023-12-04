package com.central.oauth2.common.util;

import cn.hutool.extra.spring.SpringUtil;
import com.central.common.constant.CommonConstant;
import com.central.common.constant.SecurityConstants;
import com.central.common.model.LoginAppUser;
import com.central.common.utils.LoginUserUtils;
import com.central.oauth2.common.exception.CustomOAuth2AuthenticationException;
import com.central.oauth2.common.token.BaseAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;

/**
 * 认证授权相关工具类
 *
 * @author zlt
 * @date 2018/5/13
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
public class AuthUtils {
    private AuthUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final String BASIC_ = "Basic ";

    /**
     * 获取requet(head/param)中的token
     * @param request
     * @return
     */
    public static String extractToken(HttpServletRequest request) {
        String token = extractHeaderToken(request);
        if (token == null) {
            token = request.getParameter(OAuth2ParameterNames.ACCESS_TOKEN);
            if (token == null) {
                log.debug("Token not found in request parameters.  Not an OAuth2 request.");
            }
        }
        return token;
    }

    /**
     * 解析head中的token
     * @param request
     * @return
     */
    private static String extractHeaderToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(CommonConstant.TOKEN_HEADER);
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if ((value.startsWith(OAuth2AccessToken.TokenType.BEARER.getValue()))) {
                String authHeaderValue = value.substring(OAuth2AccessToken.TokenType.BEARER.getValue().length()).trim();
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }
        return null;
    }

    /**
     * 校验accessToken
     */
    public static LoginAppUser checkAccessToken(HttpServletRequest request) {
        String accessToken = extractToken(request);
        return checkAccessToken(accessToken);
    }

    public static LoginAppUser checkAccessToken(String accessTokenValue) {
        OAuth2Authorization authorization = checkAccessTokenToAuth(accessTokenValue);
        Authentication authentication = (Authentication)authorization.getAttributes().get(Principal.class.getName());
        if (authentication == null) {
            throw new OAuth2IntrospectionException("Invalid access token: " + accessTokenValue);
        }
        return LoginUserUtils.setContext(authentication);
    }

    public static OAuth2Authorization checkAccessTokenToAuth(String accessTokenValue) {
        if (accessTokenValue == null) {
            throw new OAuth2IntrospectionException("Invalid access token: " + null);
        }
        OAuth2AuthorizationService authorizationService = SpringUtil.getBean(OAuth2AuthorizationService.class);
        OAuth2Authorization authorization = authorizationService.findByToken(accessTokenValue, OAuth2TokenType.ACCESS_TOKEN);
        if (authorization == null || authorization.getAccessToken() == null) {
            throw new OAuth2IntrospectionException("Invalid access token: " + accessTokenValue);
        } else if (authorization.getAccessToken().isExpired()) {
            authorizationService.remove(authorization);
            throw new OAuth2IntrospectionException("Access token expired: " + accessTokenValue);
        }
        return authorization;
    }

    /**
     * *从header 请求中的clientId:clientSecret
     */
    public static String[] extractClient(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BASIC_)) {
            throw new CustomOAuth2AuthenticationException("The client information in the request header is empty");
        }
        return extractHeaderClient(header);
    }

    public static String extractClientId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BASIC_)) {
            throw new CustomOAuth2AuthenticationException("The client information in the request header is empty");
        }
        String[] clientArr = extractHeaderClient(header);
        return clientArr[0];
    }

    /**
     * 从header 请求中的clientId:clientSecret
     *
     * @param header header中的参数
     */
    public static String[] extractHeaderClient(String header) {
        byte[] base64Client = header.substring(BASIC_.length()).getBytes(StandardCharsets.UTF_8);
        byte[] decoded = Base64.getDecoder().decode(base64Client);
        String clientStr = new String(decoded, StandardCharsets.UTF_8);
        String[] clientArr = clientStr.split(":");
        if (clientArr.length != 2) {
            throw new CustomOAuth2AuthenticationException("Invalid basic authentication token");
        }
        return clientArr;
    }

    /**
     * 获取登陆的帐户类型
     */
    public static String getAccountType(Authentication authentication) {
        String accountType = null;
        if (authentication != null) {
            if (authentication instanceof BaseAuthenticationToken authenticationToken) {
                accountType = (String)authenticationToken.getAdditionalParameters().get(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME);
            }
        }
        return accountType;
    }
}
