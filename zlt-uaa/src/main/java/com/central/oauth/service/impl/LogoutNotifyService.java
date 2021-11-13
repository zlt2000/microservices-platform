package com.central.oauth.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.central.oauth.utils.UsernameHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

/**
 * 登出通知服务
 *
 * @author zlt
 * @version 1.0
 * @date 2021/10/21
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@Service
public class LogoutNotifyService {
    private final static String LOGOUT_NOTIFY_URL_KEY = "LOGOUT_NOTIFY_URL_LIST";

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private TaskExecutor taskExecutor;

    @Resource
    private RedisClientDetailsService redisClientDetailsService;

    @Resource
    private TokenStore tokenStore;

    public void notification() {
        Set<String> urls = this.getLogoutNotifyUrl();
        for (String url : urls) {
            taskExecutor.execute(() -> {
                try {
                    restTemplate.getForObject(url, Void.class);
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            });
        }
    }

    /**
     * 获取登出需要通知的地址集合
     */
    private Set<String> getLogoutNotifyUrl() {
        String username = UsernameHolder.getContext();
        Set<String> logoutNotifyUrls = new HashSet<>();
        try {
            List<ClientDetails> clientDetails = redisClientDetailsService.listClientDetails();
            Map<String, Object> informationMap;
            for (ClientDetails client : clientDetails) {
                informationMap = client.getAdditionalInformation();
                String urls = (String)informationMap.get(LOGOUT_NOTIFY_URL_KEY);
                if (StrUtil.isNotEmpty(urls)) {
                    Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(client.getClientId(), username);
                    String tokenStr = getTokenValueStr(tokens);
                    if (StrUtil.isNotEmpty(tokenStr)) {
                        logoutNotifyUrls.addAll(generateNotifyUrls(urls.split(","), tokenStr));
                    }
                }
            }
        } finally {
            UsernameHolder.clearContext();
        }
        return logoutNotifyUrls;
    }

    private String getTokenValueStr(Collection<OAuth2AccessToken> tokens) {
        if (CollUtil.isNotEmpty(tokens)) {
            return StrUtil.join( ",", tokens);
        }
        return null;
    }

    private Set<String> generateNotifyUrls(String[] urls, String tokenStr) {
        Set<String> urlSet = new HashSet(urls.length);
        for (String url : urls) {
            StringBuilder urlBuilder = new StringBuilder(url);
            if (url.contains("?")) {
                urlBuilder.append("&");
            } else {
                urlBuilder.append("?");
            }
            urlBuilder.append("tokens=").append(tokenStr);
            urlSet.add(urlBuilder.toString());
        }
        return urlSet;
    }
}
