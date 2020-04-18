package com.sso.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登出成功处理类
 *
 * @author zlt
 * @date 2020/3/10
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Component
public class SsoLogoutSuccessHandler implements LogoutSuccessHandler {
	@Value("${zlt.logout-uri:''}")
	private String logoutUri;

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		OAuth2Authentication oauth2Authentication = (OAuth2Authentication)authentication;
		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)oauth2Authentication.getDetails();
		String accessToken = details.getTokenValue();
		redirectStrategy.sendRedirect(request, response, logoutUri+accessToken);
	}
}
