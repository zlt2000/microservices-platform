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
    private boolean urlEnabled = false;
}
