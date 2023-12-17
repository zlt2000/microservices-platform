package com.central.oauth.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: zlt
 * @date: 2023/11/14
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class OAuthEndpointUtils {
    public static final String ACCESS_TOKEN_REQUEST_ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
    private OAuthEndpointUtils() {
    }

    public static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
        parameterMap.forEach((key, values) -> {
            if (values.length > 0) {
                for (String value : values) {
                    parameters.add(key, value);
                }
            }
        });
        return parameters;
    }

    public static String getParam(MultiValueMap<String, String> parameters, String paramName) {
        String paramValue = parameters.getFirst(paramName);
        if (!StringUtils.hasText(paramValue) || parameters.get(paramName).size() != 1) {
            OAuthEndpointUtils.throwError(
                    OAuth2ErrorCodes.INVALID_REQUEST,
                    paramName,
                    OAuthEndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }
        return paramValue;
    }

    public static Map<String, Object> getParametersIfMatchesAuthorizationCodeGrantRequest(HttpServletRequest request, String... exclusions) {
        if (!matchesAuthorizationCodeGrantRequest(request)) {
            return Collections.emptyMap();
        }
        MultiValueMap<String, String> multiValueParameters = getParameters(request);
        for (String exclusion : exclusions) {
            multiValueParameters.remove(exclusion);
        }

        Map<String, Object> parameters = new HashMap<>();
        multiValueParameters.forEach((key, value) ->
                parameters.put(key, (value.size() == 1) ? value.get(0) : value.toArray(new String[0])));

        return parameters;
    }

    public static boolean matchesAuthorizationCodeGrantRequest(HttpServletRequest request) {
        return AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(
                request.getParameter(OAuth2ParameterNames.GRANT_TYPE)) &&
                request.getParameter(OAuth2ParameterNames.CODE) != null;
    }

    public static boolean matchesPkceTokenRequest(HttpServletRequest request) {
        return matchesAuthorizationCodeGrantRequest(request) &&
                request.getParameter(PkceParameterNames.CODE_VERIFIER) != null;
    }

    public static void throwError(String errorCode, String parameterName, String errorUri) {
        OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName, errorUri);
        throw new OAuth2AuthenticationException(error);
    }

    public static String normalizeUserCode(String userCode) {
        Assert.hasText(userCode, "userCode cannot be empty");
        StringBuilder sb = new StringBuilder(userCode.toUpperCase().replaceAll("[^A-Z\\d]+", ""));
        Assert.isTrue(sb.length() == 8, "userCode must be exactly 8 alpha/numeric characters");
        sb.insert(4, '-');
        return sb.toString();
    }
}
