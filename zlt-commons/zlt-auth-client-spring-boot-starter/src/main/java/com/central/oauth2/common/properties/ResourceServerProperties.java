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
     * token格式: reference 引用令牌(不透明), self-contained 自包含令牌(jwt))
     */
    private String tokenFormat = "reference";
}
