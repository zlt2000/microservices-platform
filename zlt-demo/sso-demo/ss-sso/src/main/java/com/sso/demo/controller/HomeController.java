package com.sso.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zlt
 * @date 2020/2/22
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Controller
public class HomeController {
    @GetMapping("/")
    public String home(ModelMap modelMap, Authentication authentication) {
        OAuth2Authentication oauth2Authentication = (OAuth2Authentication)authentication;
        modelMap.put("username", oauth2Authentication.getName());
        modelMap.put("authorities", oauth2Authentication.getAuthorities());
        modelMap.put("clientId", oauth2Authentication.getOAuth2Request().getClientId());
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)oauth2Authentication.getDetails();
        modelMap.put("token", details.getTokenValue());
        return "index";
    }
}
