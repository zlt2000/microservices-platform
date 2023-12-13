package com.central.oauth2.common.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * 资源服务器配置
 *
 * @author: zlt
 * @date: 2023/11/26
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Setter
@Getter
public class ResourceServerProperties {
    /**
     * 令牌类型:
     *  - redis: 通过 redis 来验证
     *  - inMemory: 通过本地内存来验证
     *  - jwt: 自包含令牌，自己解析内容
     *  - reference: 引用令牌，通过远程调用授权服务器进行验证
     */
    private String tokenType = "redis";
}
