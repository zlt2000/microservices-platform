package com.central.common.properties;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 示例
 *
 * @author jarvis create by 2023/1/8
 */
@ConfigurationProperties(prefix = "zlt.datascope")
@Getter
public class DataScopeProperties {
    /**
     * 是否开启权限控制
     */
    private Boolean enabled = Boolean.FALSE;
    /**
     * 在includeTables的匹配符中过滤某几个表不需要权限的，仅enabled=true
     */
    private Set<String> ignoreTables = Collections.singleton("SYS*");
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
    private String creatorIdColumnName;

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setIgnoreTables(Set<String> ignoreTables) {
        HashSet<String> ignoreSet = new HashSet<>();
        CollUtil.addAll(ignoreSet, ignoreTables);
        CollUtil.addAll(ignoreSet, this.ignoreTables);
        this.ignoreTables = ignoreSet;
    }

    public void setIgnoreSqls(Set<String> ignoreSqls) {
        this.ignoreSqls = ignoreSqls;
    }

    public void setIncludeTables(Set<String> includeTables) {
        this.includeTables = includeTables;
    }

    public void setCreatorIdColumnName(String creatorIdColumnName) {
        this.creatorIdColumnName = creatorIdColumnName;
    }
}
