package com.central.common.datascope.mp.interceptor;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;

/**
 * 示例
 *
 * @author jarvis create by 2023/2/2
 */
@Slf4j
public class EnableQuerySqlLogInnerInterceptor implements InnerInterceptor{
    private InnerInterceptor delegate;

    public EnableQuerySqlLogInnerInterceptor(InnerInterceptor delegate) {
        Assert.notNull(delegate, "委派类不能为空");
        this.delegate = delegate;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        String sql = boundSql.getSql();
        log.info("执行mapperId{},原始sql为{}", ms.getId(), sql);
        delegate.beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
        log.info("执行mapperId{}, 修改sql为{}", ms.getId(), mpBs.sql());
    }
}
