package com.central.oauth.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.central.oauth.utils.UsernameHolder;
import com.central.oauth2.common.pojo.ClientDto;
import com.central.oauth2.common.service.impl.RedisOAuth2AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 统一登出服务
 *
 * @author zlt
 * @version 1.0
 * @date 2023/11/18
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@Service
public class UnifiedLogoutService {
    private final static String LOGOUT_NOTIFY_URL_KEY = "LOGOUT_NOTIFY_URL_LIST";

    private final RestTemplate restTemplate;

    private final TaskExecutor taskExecutor;

    private final RegisteredClientService clientService;

    private final RedisOAuth2AuthorizationService authorizationService;

    public UnifiedLogoutService(RestTemplate restTemplate, TaskExecutor taskExecutor
            , RegisteredClientService clientService
            , @Autowired(required = false) RedisOAuth2AuthorizationService authorizationService) {
        this.restTemplate = restTemplate;
        this.taskExecutor = taskExecutor;
        this.clientService = clientService;
        this.authorizationService = authorizationService;
    }

    public void allLogout() {
        if (authorizationService == null) {
            throw new RuntimeException("the 'zlt.oauth2.token.store' parameter must be redis");
        }

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
            List<ClientDto> clientDetails = clientService.list();
            Map<String, Object> informationMap;
            for (ClientDto client : clientDetails) {
                informationMap = this.getInfoMap(client.getAdditionalInformation());
                String urls = (String)informationMap.get(LOGOUT_NOTIFY_URL_KEY);
                if (StrUtil.isNotEmpty(urls)) {
                    List<String> tokens = authorizationService.findTokensByClientIdAndUserName(client.getClientId(), username);
                    if (CollUtil.isNotEmpty(tokens)) {
                        //注销所有该用户名下的token
                        tokens.forEach(authorizationService::remove);
                        String tokenStr = getTokenValueStr(tokens);
                        logoutNotifyUrls.addAll(generateNotifyUrls(urls.split(","), tokenStr));
                    }
                }
            }
        } finally {
            UsernameHolder.clearContext();
        }
        return logoutNotifyUrls;
    }

    private String getTokenValueStr(List<String> tokens) {
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

    private Map<String, Object> getInfoMap(String additionalInformation) {
        if (StrUtil.isNotEmpty(additionalInformation)) {
            return JSONUtil.toBean(additionalInformation, Map.class);
        }
        return Map.of();
    }
}
