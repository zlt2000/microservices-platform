package com.central.common.enums;

import lombok.Getter;

/**
 * 枚举类型
 *
 * @author jarvis create by 2023/1/10
 */
@Getter
public enum DataScope implements ZltEnum{
    ALL(0, "全部权限"), CREATOR(1, "创建者权限");

    DataScope(Integer id, String content) {
        this.id = id;
        this.content = content;
    }

    private Integer id;
    private String content;
}
