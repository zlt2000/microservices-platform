package com.central.oauth.component;

import com.central.common.constant.SecurityConstants;
import com.central.common.model.LoginAppUser;
import com.central.oauth2.common.token.BaseAuthenticationToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

/**
 * token 输出增强
 *
 * @author: zlt
 * @date: 2023/12/2
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class CustomeOAuth2TokenCustomizer implements OAuth2TokenCustomizer<OAuth2TokenClaimsContext> {
	@Override
	public void customize(OAuth2TokenClaimsContext context) {
		OAuth2TokenClaimsSet.Builder claims = context.getClaims();
		AbstractAuthenticationToken authenticationToken = context.getAuthorizationGrant();

		if (authenticationToken instanceof BaseAuthenticationToken baseAuthenticationToken) {
			LoginAppUser user = (LoginAppUser) context.getPrincipal().getPrincipal();
			claims.claim(SecurityConstants.DETAILS_USER_ID, user.getId());
			claims.claim(SecurityConstants.USERNAME, user.getUsername());

			String account_type = (String)baseAuthenticationToken.getAdditionalParameters().get(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME);
			claims.claim(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME, account_type);
		}
	}
}
