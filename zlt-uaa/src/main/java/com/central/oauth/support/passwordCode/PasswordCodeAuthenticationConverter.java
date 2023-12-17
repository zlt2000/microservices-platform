package com.central.oauth.support.passwordCode;

import com.central.oauth.support.base.BaseAuthenticationConverter;
import com.central.oauth.utils.OAuthEndpointUtils;
import com.central.oauth2.common.token.BaseAuthenticationToken;
import com.central.oauth2.common.token.PasswordCodeAuthenticationToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * @author: zlt
 * @date: 2023/11/18
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class PasswordCodeAuthenticationConverter extends BaseAuthenticationConverter {
    private final static String PARAM_DEVICEID = "deviceId";
    private final static String PARAM_VALIDCODE = "validCode";
    @Override
    protected String supportGrantType() {
        return PasswordCodeAuthenticationToken.GRANT_TYPE.getValue();
    }

    @Override
    protected List<String> paramNames() {
        return List.of(OAuth2ParameterNames.USERNAME, OAuth2ParameterNames.PASSWORD
                , PARAM_DEVICEID, PARAM_VALIDCODE);
    }

    @Override
    protected BaseAuthenticationToken getToken(MultiValueMap<String, String> parameters) {
        String username = OAuthEndpointUtils.getParam(parameters, OAuth2ParameterNames.USERNAME);
        String password = OAuthEndpointUtils.getParam(parameters, OAuth2ParameterNames.PASSWORD);
        String deviceId = OAuthEndpointUtils.getParam(parameters, PARAM_DEVICEID);
        String validCode = OAuthEndpointUtils.getParam(parameters, PARAM_VALIDCODE);
        return new PasswordCodeAuthenticationToken(username, password, deviceId, validCode);
    }
}
