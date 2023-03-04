package com.central.common.properties;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.ImmutableSet;
import lombok.Data;
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
@Data
public class DataScopeProperties {
    private static final Set<String> INGORE_SQL_ID = ImmutableSet
            .of("com.central.user.mapper.findRolesByUserId"
                    , "com.central.user.mapper.SysUserMapper.selectList"
                    , "com.central.user.mapper.SysUserRoleMapper.findRolesByUserId"
                    , "com.central.user.mapper.SysRoleMenuMapper.findMenusByRoleIds");
    /**
     * 是否开启权限控制
     */
    private Boolean enabled = Boolean.FALSE;

    /**
     * 是否开启打印sql的修改情况
     */
    private Boolean enabledSqlDebug = Boolean.FALSE;
    /**
     * 配置那些表不执行权限控制
     */
    private Set<String> ignoreTables = Collections.emptySet();
    /**
     * 指定那些sql不执行权限控制
     */
    private Set<String> ignoreSqls = INGORE_SQL_ID;
    /**
     * 配置那些表执行数据权限控制，默认是*则表示全部
     */
    private Set<String> includeTables = Collections.singleton("*");
    /**
     * 指定那些sql执行数据权限控制
     * 1. 为空时：所有sql都添加权限控制
     * 2. 有值时：只有配置的sql添加权限控制
     */
    private Set<String> includeSqls = Collections.emptySet();

    /**
     * 指定创建人id的字段名
     */
    private String creatorIdColumnName = "creator_id";

    public void setIgnoreSqls(Set<String> ignoreSqls) {
        Set<String> ingoreSet = new HashSet<>();
        ingoreSet.addAll(INGORE_SQL_ID);
        if(CollUtil.isNotEmpty(ignoreSqls)){
            ingoreSet.addAll(ignoreSqls);
        }
        this.ignoreSqls = ingoreSet;
    }
}
