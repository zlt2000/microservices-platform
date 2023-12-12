package com.central.oauth.support.openid;

import com.central.oauth.support.base.BaseAuthenticationConverter;
import com.central.oauth.utils.OAuthEndpointUtils;
import com.central.oauth2.common.token.BaseAuthenticationToken;
import com.central.oauth2.common.token.OpenIdAuthenticationToken;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * @author: zlt
 * @date: 2023/11/18
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class OpenIdAuthenticationConverter extends BaseAuthenticationConverter {
    private final static String PARAM_OPENID = "openId";

    @Override
    protected String supportGrantType() {
        return OpenIdAuthenticationToken.GRANT_TYPE.getValue();
    }

    @Override
    protected List<String> paramNames() {
        return List.of(PARAM_OPENID);
    }

    @Override
    protected BaseAuthenticationToken getToken(MultiValueMap<String, String> parameters) {
        String openId = OAuthEndpointUtils.getParam(parameters, PARAM_OPENID);
        return new OpenIdAuthenticationToken(openId);
    }
}
