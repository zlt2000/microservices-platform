package com.central.db.interceptor;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.List;

/**
 * MyBatis-plus租户拦截器
 *
 * @author zlt
 * @version 1.0
 * @date 2022/5/6
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class CustomTenantInterceptor extends TenantLineInnerInterceptor {
    private List<String> ignoreSqls;

    public CustomTenantInterceptor(TenantLineHandler tenantLineHandler, List<String> ignoreSqls) {
        super(tenantLineHandler);
        this.ignoreSqls = ignoreSqls;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds
            , ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (isIgnoreMappedStatement(ms.getId())) {
            return;
        }
        super.beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
    }

    private boolean isIgnoreMappedStatement(String msId) {
        return ignoreSqls.stream().anyMatch((e) -> e.equalsIgnoreCase(msId));
    }
}
