package com.central.oauth.handler;

import cn.hutool.core.util.StrUtil;
import com.central.common.utils.ResponseUtil;
import com.central.oauth.service.impl.UnifiedLogoutService;
import com.central.oauth2.common.properties.SecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author zlt
 * @date 2020/3/10
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class OauthLogoutSuccessHandler implements LogoutSuccessHandler {
	private static final String REDIRECT_URL = "redirect_url";
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private final UnifiedLogoutService unifiedLogoutService;
	private final SecurityProperties securityProperties;
	private final ObjectMapper objectMapper;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		if (securityProperties.getAuth().getUnifiedLogout()) {
			unifiedLogoutService.allLogout();
		}

		String redirectUri = request.getParameter(REDIRECT_URL);
		if (StrUtil.isNotEmpty(redirectUri)) {
			//重定向指定的地址
			redirectStrategy.sendRedirect(request, response, redirectUri);
		} else {
			ResponseUtil.responseWriter(objectMapper, response, "登出成功", 0);
		}
	}
}
