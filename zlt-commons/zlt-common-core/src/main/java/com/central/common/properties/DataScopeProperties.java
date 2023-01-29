package com.central.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 示例
 *
 * @author jarvis create by 2023/1/8
 */
@ConfigurationProperties(prefix = "zlt.datascope")
@Data
public class DataScopeProperties {
    /**
     * 是否开启权限控制
     */
    private Boolean enabled = Boolean.FALSE;
    /**
     * 在includeTables的匹配符中过滤某几个表不需要权限的，仅enabled=true
     */
    private Set<String> ignoreTables = Collections.emptySet();
    /**
     * 指定某几条sql不执行权限控制， 仅enabled=true生效
     */
    private Set<String> ignoreSqls = Collections.emptySet();
    /**
     * 指定某几个表接受权限控制，仅enabled=true，默认当开启时全部表
     */
    private Set<String> includeTables = Collections.singleton("*");

    /**
     * 指定需要的字段名
     */
    private String scopeColumn;
}
