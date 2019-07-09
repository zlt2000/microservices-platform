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
     * 要认证的url
     */
    private String[] httpUrls = {};

    /**
     * 是否开启url权限验证
     */
    private Boolean urlEnabled = false;

    /**
     * token自动续签配置（目前只有redis实现）
     */
    private RenewProperties renew = new RenewProperties();
}
