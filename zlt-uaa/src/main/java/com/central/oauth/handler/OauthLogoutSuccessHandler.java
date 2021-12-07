package com.central.oauth.handler;

import cn.hutool.core.util.StrUtil;
import com.central.common.model.Result;
import com.central.common.utils.JsonUtil;
import com.central.oauth.service.impl.UnifiedLogoutService;
import com.central.oauth2.common.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zlt
 * @date 2020/3/10
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
public class OauthLogoutSuccessHandler implements LogoutSuccessHandler {
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Resource
	private UnifiedLogoutService unifiedLogoutService;

	@Resource
	private SecurityProperties securityProperties;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		if (securityProperties.getAuth().getUnifiedLogout()) {
			unifiedLogoutService.allLogout();
		}

		String redirectUri = request.getParameter("redirect_uri");
		if (StrUtil.isNotEmpty(redirectUri)) {
			//重定向指定的地址
			redirectStrategy.sendRedirect(request, response, redirectUri);
		} else {
			response.setStatus(HttpStatus.OK.value());
			response.setCharacterEncoding("UTF-8");
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			PrintWriter writer = response.getWriter();
			String jsonStr = JsonUtil.toJSONString(Result.succeed("登出成功"));
			writer.write(jsonStr);
			writer.flush();
		}
	}
}
