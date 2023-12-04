package com.central.oauth2.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * token 类型
 *
 * @author: zlt
 * @date: 2023/12/4
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Getter
@AllArgsConstructor
public enum TokenType {
    REDIS("redis"),
    MEMORY("inMemory"),
    JWT("jwt"),
    REF("reference");
    private final String name;
}
