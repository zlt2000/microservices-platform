package com.central.gateway.monitor.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * @author zlt
 * @date 2019/1/8
 */
@Configuration
public class SecuritySecureConfig {
    private final String adminContextPath;

    public SecuritySecureConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminContextPath + "/");

        http.authorizeHttpRequests(authorizeRequests -> {
            // 授权服务器关闭basic认证
            authorizeRequests
                    .requestMatchers(adminContextPath + "/assets/**").permitAll()
                    .requestMatchers(adminContextPath + "/login").permitAll()
                    .anyRequest().authenticated();
        })
        .formLogin(formLogin -> formLogin.loginPage(adminContextPath + "/login").successHandler(successHandler))
        .logout(logout -> logout.logoutUrl(adminContextPath + "/logout"))
        .httpBasic(Customizer.withDefaults())
        .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
