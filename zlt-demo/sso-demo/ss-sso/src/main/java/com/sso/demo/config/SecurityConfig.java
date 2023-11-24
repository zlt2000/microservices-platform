package com.sso.demo.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;

/**
 * security配置
 *
 * @author zlt
 * @date 2020/2/22
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@EnableOAuth2Sso
@Configuration
public class SecurityConfig {
    @Value("${security.oauth2.sso.login-path:}")
    private String loginPath;

    @Resource
    private LogoutSuccessHandler ssoLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /*http.authorizeHttpRequests().anyRequest().authenticated()
                .and()
                    .csrf().disable()
                .logout()
                    .logoutSuccessHandler(ssoLogoutSuccessHandler);
        if (StrUtil.isNotEmpty(loginPath)) {
            http.formLogin().loginProcessingUrl(loginPath);
        }*/
        return http.build();
    }
}