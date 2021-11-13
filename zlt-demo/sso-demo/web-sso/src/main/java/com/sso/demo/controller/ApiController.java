package com.sso.demo.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author zlt
 * @date 2020/3/10
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@RestController
public class ApiController {
    @Value("${zlt.sso.client-id:}")
    private String clientId;

    @Value("${zlt.sso.client-secret:}")
    private String clientSecret;

    @Value("${zlt.sso.redirect-uri:}")
    private String redirectUri;

    @Value("${zlt.sso.access-token-uri:}")
    private String accessTokenUri;

    @Value("${zlt.sso.user-info-uri:}")
    private String userInfoUri;

    private final static Map<String, Map<String, Object>> localTokenMap = new HashMap<>();

    @GetMapping("/token/{code}")
    public String tokenInfo(@PathVariable String code) throws UnsupportedEncodingException {
        //获取token
        Map tokenMap = getAccessToken(code);
        String accessToken = (String)tokenMap.get("access_token");
        //获取用户信息
        Map userMap = getUserInfo(accessToken);
        List<String> roles = getRoles(userMap);

        Map result = new HashMap(2);
        String username = (String)userMap.get("username");
        result.put("username", username);
        result.put("roles", roles);
        localTokenMap.put(accessToken, result);

        return accessToken;
    }

    /**
     * 获取token
     */
    public Map getAccessToken(String code) throws UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        byte[] authorization = (clientId + ":" + clientSecret).getBytes("UTF-8");
        BASE64Encoder encoder = new BASE64Encoder();
        String base64Auth = encoder.encode(authorization);
        headers.add("Authorization", "Basic " + base64Auth);

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("code", code);
        param.add("grant_type", "authorization_code");
        param.add("redirect_uri", redirectUri);
        param.add("scope", "app");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(accessTokenUri, request , Map.class);
        Map result = response.getBody();
        return result;
    }

    /**
     * 获取用户信息
     */
    public Map getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        Map result = restTemplate.getForObject(userInfoUri+"?access_token="+accessToken, Map.class);
        return (Map)result.get("datas");
    }

    private List<String> getRoles(Map userMap) {
        List<Map<String, String>> roles = (List<Map<String, String>>)userMap.get("roles");
        List<String> result = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(roles)) {
            roles.forEach(e -> {
                result.add(e.get("code"));
            });
        }
        return result;
    }

    @GetMapping("/user")
    public Map<String, Object> user(HttpServletRequest request) {
        String token = request.getParameter("access_token");
        return localTokenMap.get(token);
    }

    @GetMapping("/logoutNotify")
    public void logoutNotify(HttpServletRequest request) {
        String tokens = request.getParameter("tokens");
        log.info("=====logoutNotify: " + tokens);
        if (StrUtil.isNotEmpty(tokens)) {
            for (String accessToken : tokens.split(",")) {
                localTokenMap.remove(accessToken);
            }
        }
    }
}
