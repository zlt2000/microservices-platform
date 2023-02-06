package com.central.oauth.filter;

import com.central.common.constant.SecurityConstants;
import com.central.common.context.TenantContextHolder;
import com.central.common.model.Result;
import com.central.oauth.handler.decryptParamHandler.IDecryptParamHandler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.Map;

/**
 * oauth-token拦截器
 * 1. 赋值租户
 * 2. 统一返回token格式
 *
 * @author zlt
 * @date 2020/3/29
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@Component
@Aspect
public class OauthTokenAspect {
    @Resource
    private IDecryptParamHandler decryptParamHandler;

    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
    public Object handleControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object[] args = joinPoint.getArgs();
            Principal principal = (Principal) args[0];
            if (!(principal instanceof Authentication)) {
                throw new OAuth2Exception(
                        "There is no client authentication. Try adding an appropriate authentication filter.");
            }
            String clientId = getClientId(principal);
            Map<String, String> parameters = (Map<String, String>) args[1];
            //解密参数
            decryptParamHandler.decryptParams(parameters);

            String grantType = parameters.get(OAuth2Utils.GRANT_TYPE);
            if (!parameters.containsKey(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME)) {
                parameters.put(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME, SecurityConstants.DEF_ACCOUNT_TYPE);
            }

            //保存租户id
            TenantContextHolder.setTenant(clientId);
            Object proceed = joinPoint.proceed(args);
            if (SecurityConstants.AUTHORIZATION_CODE.equals(grantType)) {
                /*
                 如果使用 @EnableOAuth2Sso 注解不能修改返回格式，否则授权码模式可以统一改
                 因为本项目的 sso-demo/ss-sso 里面使用了 @EnableOAuth2Sso 注解，所以这里就不修改授权码模式的token返回值了
                 */
                return proceed;
            } else {
                ResponseEntity<OAuth2AccessToken> responseEntity = (ResponseEntity<OAuth2AccessToken>) proceed;
                OAuth2AccessToken body = responseEntity.getBody();
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(Result.succeed(body));
            }
        } finally {
            TenantContextHolder.clear();
        }
    }

    private String getClientId(Principal principal) {
        Authentication client = (Authentication) principal;
        if (!client.isAuthenticated()) {
            throw new OAuth2Exception("The client is not authenticated.");
        }
        String clientId = client.getName();
        if (client instanceof OAuth2Authentication) {
            clientId = ((OAuth2Authentication) client).getOAuth2Request().getClientId();
        }
        return clientId;
    }
}
