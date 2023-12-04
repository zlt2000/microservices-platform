package com.central.oauth2.common.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置需要放权的url白名单
 *
 * @author zlt
 * @version 1.0
 * @date 2019/1/19
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Setter
@Getter
public class PermitProperties {
    /**
     * 监控中心和swagger需要访问的url
     */
    private static final String[] ENDPOINTS = {
            "/login.html", "/validata/code/**",
            "/v3/api-docs/**", "/*/v3/api-docs/**",
            "/doc.html", "/swagger-ui.html", "/swagger-ui/**",
            "/actuator/**", "/webjars/**", "/druid/**",
            "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error",
    };

    /**
     * 设置不用认证的url
     */
    private String[] httpUrls = {};

    public String[] getUrls() {
        if (httpUrls == null || httpUrls.length == 0) {
            return ENDPOINTS;
        }
        List<String> list = new ArrayList<>();
        for (String url : ENDPOINTS) {
            list.add(url);
        }
        for (String url : httpUrls) {
            list.add(url);
        }
        return list.toArray(new String[list.size()]);
    }
}
