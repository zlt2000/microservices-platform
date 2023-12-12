package com.central.oauth.config;

import com.central.common.constant.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * @author: zlt
 * @date: 2023/11/23
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@RequiredArgsConstructor
public class FormIdentityLoginConfigurer extends AbstractHttpConfigurer<FormIdentityLoginConfigurer, HttpSecurity> {
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final LogoutHandler logoutHandler;

    @Override
    public void init(HttpSecurity http) throws Exception {
        http.formLogin(formLogin -> formLogin
                .loginPage(SecurityConstants.LOGIN_PAGE)
                .loginProcessingUrl(SecurityConstants.OAUTH_LOGIN_PRO_URL)
                .successHandler(new SavedRequestAwareAuthenticationSuccessHandler()))
                .logout(logout -> logout
                        .logoutUrl(SecurityConstants.LOGOUT_URL)
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .addLogoutHandler(logoutHandler)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true))
                .headers(header -> header
                        // 避免iframe同源无法登录许iframe
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
    }
}
