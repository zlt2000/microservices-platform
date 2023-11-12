package com.central.oauth2.common.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * 认证配置
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

    /**
     * 是否同应用同账号登录互踢
     */
    private Boolean isSingleLogin = false;

    /**
     * 是否同应用同账号登录时共用token
     * true: 多个用户使用同一账号登录时共用一个token
     * false: 就算使用同一账号登录时都会新建一个token
     */
    private Boolean isShareToken = true;

    /**
     * 参数加密(rsa)，对应的私钥(用于解密)
     * 默认私钥对应的公钥为：MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6u4sP969hZP7BUEeAXJlq5wEmZ2CIZU4+5JADY8Ium55qGaE5qXEwMaV+M2HFWU4PZbHfH+RGEIMwjkARRok93krFnJuuwTjLwgyUSsKm5M7v3Ek8zdqs474v1qOxqE6BmHz9QJfgnAtFYxwEyVRoQ12+IAhpOzDU3rz02VR05kGCrgGU6szxDtp6cQ+u9ACGPy/uKdIQ6H7aM/oxMyPlwK9H38ni6Lxai7q56qp6F1p7drxh8CWJZ3j0NicB5ZPnOMtrGL5lfnifHBjB+CDJXv8kffY0zwL3J+LrnyFbpKeNeMJZpykoYW85Pdz+8WnE9KGMM3EeOeD/QX/OfMdFQIDAQAB
     */
    private String decryptParamPrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDq7iw/3r2Fk/sFQR4BcmWrnASZnYIhlTj7kkANjwi6bnmoZoTmpcTAxpX4zYcVZTg9lsd8f5EYQgzCOQBFGiT3eSsWcm67BOMvCDJRKwqbkzu/cSTzN2qzjvi/Wo7GoToGYfP1Al+CcC0VjHATJVGhDXb4gCGk7MNTevPTZVHTmQYKuAZTqzPEO2npxD670AIY/L+4p0hDoftoz+jEzI+XAr0ffyeLovFqLurnqqnoXWnt2vGHwJYlnePQ2JwHlk+c4y2sYvmV+eJ8cGMH4IMle/yR99jTPAvcn4uufIVukp414wlmnKShhbzk93P7xacT0oYwzcR454P9Bf858x0VAgMBAAECggEBAJxK94VGWi+T01wbhirQQHN6yFSqRPiyncY/9f0PO29MMAOosKIBhnP5qaxsj4HcZR4UQYLCG3VX+8T6xwMx8YXyRogYeTJSfhG8Ej2NtPDrcsRaMYrdQ09RvosPZA0hFclJQVOu0HumxVegpq8WFMhgfNW16KwgF5JiKfRpY5aw4NDWZFmdNExdymrWPheI8pWBq/U+l8oqrekhbiEKXM2UXmLlKTS9Nk46LRYwaaiDW8JEFuPdx0cnakb+ecCXGd/8Cc8Hxn/mLyvqS1cWHT3J+lXRkfcnNnrJTR9qhf9l077XBMJqVckFEpK4kKbXHZd957ISAxq48Tm+xMX9KAECgYEA995szPToU0BxTTtOD4Z4c5JvbURQEcDQl/dB+qCQnu/orW5ZWv4++lHq2SZPjzbt7M8OaoRN8A5zYcNrYe4kBhLOLqHdaS1yUGIIjAejEAJplR5GqI3T5qzaLjpyiUZpO0mOcwAazCevnSXH6uO5jP0sjwxwaXz4OsTcpHIXHqECgYEA8qMXhh+G88+vTELL/2dHhtTIf7IAJLQ37c1Xrm6uwOHPfiDr256Lc4EzF8QAQqlHoYm1jRK7xDfFymY/SJKJVhYehWlNilnMuDuDOqseC/Zn2KgjMSjLLVkbp25DcpAu6SSiWlBvemrV3jivX/MU0BFp8HjbrlUcl12lRtYgrfUCgYB+iN2iA6RWW597fbrr0gnLdgXMEgOODJBwA5l7CFzLxk1Ru/OBsCkWQJtTH2ueALyVF16UodXnpnjgf5Jh++AH+bGnvJn7B2hEAMe8NGnZ0mFz7nDDuyNhrvvyfYPa8EboLTS7IGKNtfTAlHjqQDaI8vW8UO1R7KoL1lOM33FOAQKBgQDt/Z6jReU+3CUbbiFeANWdoLSQ2+1cExEQxWsNgy8Rreux0WTG4/nwb3fIBc4jlJrYDZTwLMHTssjkv+muq1zd/ZAuV51g6LfutSEAuLseDLDLSBBMtbCkaFTBo1uw0U/SCsbcQy01K/leoMcUG//8HjiFUGZZ1s3WgloM4xbmyQKBgAipgnoEyzvUfe2OMOc5ARGNSGZG4JGTGCyfnrYvYfffWpAokklHOkZMumeSWJXkx5F+MgJd9fxBK9S57PZ09gWkoeSVg0xcz5QMjh8BswfCdyet/CXQtwIfK0Wf1gWdAxC6vvv3DQ7zXbTlvqMdVOFzCKlocCYkzWMvIsejWXnW";
}
