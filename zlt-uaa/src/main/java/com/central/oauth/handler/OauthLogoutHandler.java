package com.central.oauth.handler;

import cn.hutool.core.util.StrUtil;
import com.central.oauth.utils.UsernameHolder;
import com.central.oauth2.common.properties.SecurityProperties;
import com.central.oauth2.common.util.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author zlt
 * @date 2018/10/17
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class OauthLogoutHandler implements LogoutHandler {
	private final OAuth2AuthorizationService oAuth2AuthorizationService;
	private final SecurityProperties securityProperties;
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Assert.notNull(oAuth2AuthorizationService, "oAuth2AuthorizationService must be set");
		String token = request.getParameter("token");
		if (StrUtil.isEmpty(token)) {
			token = AuthUtils.extractToken(request);
		}
		if(StrUtil.isNotEmpty(token)){
			OAuth2Authorization existingAccessToken = oAuth2AuthorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
			if (existingAccessToken != null) {
				if (securityProperties.getAuth().getUnifiedLogout()) {
					UsernameHolder.setContext(existingAccessToken.getPrincipalName());
				}
				oAuth2AuthorizationService.remove(existingAccessToken);
				log.info("remove existingAccessToken!", existingAccessToken.getAccessToken().getToken().getTokenType());
			}
		}
	}
}
