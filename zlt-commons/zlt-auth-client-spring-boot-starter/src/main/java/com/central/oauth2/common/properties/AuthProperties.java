package com.central.oauth2.common.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * 认证配置
 *
 * @author zlt
 */
@Setter
@Getter
public class AuthProperties {
    /**
     * 配置要认证的url（默认不需要配置）
     *
     * 优先级大于忽略认证配置`zlt.security.ignore.httpUrls`
     * 意思是如果同一个url同时配置了`忽略认证`和`需要认证`，则该url还是会被认证
     */
    private String[] httpUrls = {};

    /**
     * token自动续签配置（目前只有redis实现）
     */
    private RenewProperties renew = new RenewProperties();

    /**
     * url权限配置
     */
    private UrlPermissionProperties urlPermission = new UrlPermissionProperties();

    /**
     * 是否开启统一登出
     * 1. 登出时把同一个用户名下的所有token都注销
     * 2. 登出信息通知所有单点登录系统
     */
    private Boolean unifiedLogout = false;
}
