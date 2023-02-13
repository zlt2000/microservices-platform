package com.central.common.datascope.mp.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.central.common.datascope.mp.sql.handler.SqlHandler;
import com.central.common.properties.DataScopeProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.*;

import static com.central.common.datascope.mp.sql.handler.SqlHandler.ALIAS_SYNBOL;

/**
 * 数据权限拦截器
 *
 * @author jarvis create by 2023/1/7
 */
@Slf4j
@Data
@NoArgsConstructor
public class DataScopeInnerInterceptor implements InnerInterceptor {

    private DataScopeProperties dataScopeProperties;

    /**
     * 权限的where条件
     */
    private SqlHandler sqlHandler;

    /**
     * 对表配置进行缓存，优先读取缓存，在进行匹配
     */
    private Map<String, TableConfig> tableInfoMap = new HashMap<>();

    /**
     * 通配符
     */
    private PathPatternParser pathPatternParser = new PathPatternParser();

    public DataScopeInnerInterceptor(DataScopeProperties dataScopeProperties, SqlHandler sqlHandler) {
        this.dataScopeProperties = dataScopeProperties;
        this.sqlHandler = sqlHandler;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        //为空时：所有sql都添加权限控制
        if (CollUtil.isEmpty(dataScopeProperties.getIncludeSqls())
                //有值时：只有配置的sql添加权限控制
            || dataScopeProperties.getIncludeSqls().contains(ms.getId())) {
            //判断排除的sql
            if(CollUtil.isEmpty(dataScopeProperties.getIgnoreSqls())
                    || !dataScopeProperties.getIgnoreSqls().contains(ms.getId())){
                PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
                String sql = boundSql.getSql();
                try {
                    Select select = explainQuerySql(sql);
                    reform(select.getSelectBody());
                    mpBs.sql(select.toString());
                } catch (JSQLParserException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public Select explainQuerySql(String sql) throws JSQLParserException {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(sql));
        return select;
    }

    /**
     * 递归对查询和解析后的子查询进行改造
     * @param selectBody
     * @param <T>
     * @throws JSQLParserException
     */
    public <T extends SelectBody>void reform(SelectBody selectBody) throws JSQLParserException {
        // 如果是plainSelect的话进行改造
        if(selectBody instanceof PlainSelect&& ObjectUtil.isNotNull(sqlHandler)){
            PlainSelect select = (PlainSelect) selectBody;
            // 获取权限的where条件
            String scopeWhereSql = sqlHandler.handleScopeSql();
            // 如果条件不是空的话才对select进行改造
            if(StrUtil.isNotBlank(scopeWhereSql)){
                // 需要改造的别名列表，自动增加到where条件中
                List<String> tableAliasList = new ArrayList<>();
                FromItem fromItem = select.getFromItem();
                String tableAlias = explainFromItem(fromItem);
                // 获取from的表字段，如果from是子查询则进行递归
                if(fromItem instanceof Table){

                    String upperTableName = ((Table) fromItem).getName().toUpperCase();
                    if(tableInfoMap.containsKey(upperTableName)){
                        if (!tableInfoMap.get(upperTableName).getIgnore()) {
                            tableAliasList.add(StrUtil.isNotBlank(tableAlias)? tableAlias: "");
                        }
                    }else{
                        boolean ignore = true;
                        if(isReformTable(upperTableName)){
                            tableAliasList.add(StrUtil.isNotBlank(tableAlias)? tableAlias: "");
                            ignore = false;
                        }
                        // 写入缓存
                        tableInfoMap.put(upperTableName, new TableConfig(upperTableName, ignore));
                    }
                }else if(fromItem instanceof SubSelect){
                    reform(((SubSelect) fromItem).getSelectBody());
                }
                // 获取join列表，然后获取对应的表或者递归子查询
                List<Join> joinList = select.getJoins();
                if (CollUtil.isNotEmpty(joinList)) {
                    for (Join join : joinList) {
                        if(join.getRightItem() instanceof Table){
                            String joinTable = ((Table) join.getRightItem()).getName().toUpperCase();
                            String joinAlias = ((Table) join.getRightItem()).getAlias().getName();
                            if(tableInfoMap.containsKey(joinTable)){
                                if (!tableInfoMap.get(joinTable).getIgnore()) {
                                    tableAliasList.add(StrUtil.isNotBlank(joinAlias)? joinAlias: "");
                                }
                            }else{
                                boolean ignore = true;
                                if(isReformTable(joinTable)){
                                    tableAliasList.add(StrUtil.isNotBlank(joinAlias)? joinAlias: "");
                                    ignore = false;
                                }
                                // 写入缓存
                                tableInfoMap.put(joinTable, new TableConfig(joinTable, ignore));
                            }
                        }
                        if(join.getRightItem() instanceof SubSelect){
                            reform(((SubSelect) join.getRightItem()).getSelectBody());
                        }
                    }
                }
                // 如果改造的表是空的话则不改造对应的select
                if(CollUtil.isNotEmpty(tableAliasList)){
                    reformWhere(select, scopeWhereSql, tableAliasList);
                }
            }
            // 如果select不是plainSelect的话则进行递归改造
        }else if(selectBody instanceof WithItem&& Objects.nonNull(((WithItem)selectBody).getSubSelect())){
            reform(((WithItem)selectBody).getSubSelect().getSelectBody());
        }
    }


    /**
     * 判断表是否需要改造
     * @param table
     * @return
     * 1. 判断表是否在需要改造的范围
     *      1.1 如果表在inclde的set中（是否存在没用通配符的情况写入配置）
     *      1.2 进行通配符匹配判断范围
     * 2. 在改造的范围中进行提出
     *      2.1 判断是不是完全匹配上ignore列表中
     *      2.2 判断是否在通配符过滤
     */
    private boolean isReformTable(String table){
        return
                // 1. 判断表是否在需要改造的范围
                (dataScopeProperties.getIncludeTables().contains(table)
                ||dataScopeProperties.getIncludeTables().stream().anyMatch(item->
                pathPatternParser.parse(item.toUpperCase()).matches(PathContainer.parsePath(table))
        ))&& (
                // 如果没有忽略列表的话在范围中直接返回
                CollUtil.isEmpty(dataScopeProperties.getIgnoreTables())
                        // 在改造的范围中进行忽略表
                ||!(dataScopeProperties.getIgnoreTables().contains(table)||
                dataScopeProperties.getIgnoreTables().stream().anyMatch(item->
                        pathPatternParser.parse(item.toUpperCase()).matches(PathContainer.parsePath(table))
                )));
    }

    /**
     * 解析from中的东西
     * @param fromItem
     * @return
     * @throws JSQLParserException
     */
    private String explainFromItem(FromItem fromItem) throws JSQLParserException {
        // 别名
        String alias = "";
        if(Objects.nonNull(fromItem)){
            // 如果from的东西是表的话
            if (fromItem instanceof Table) {
                Alias tablealias = ((Table) fromItem).getAlias();
                if(Objects.nonNull(tablealias)&& StrUtil.isNotBlank(tablealias.getName())){
                    alias = tablealias.getName();
                }else{
                    alias = ((Table) fromItem).getName();
                }
            }
            // 如果from的子查询
            if(fromItem instanceof SubSelect){
                SelectBody subSelectBody = ((SubSelect) fromItem).getSelectBody();
                reform(subSelectBody);
            }
        }
        return alias;
    }

    /**
     * 改造where条件
     * @param select
     * @param whereSql where 条件
     * @param aliasName 需要添加权限的表别名
     * @return
     * @throws JSQLParserException
     */
    private SelectBody reformWhere(PlainSelect select, String whereSql, List<String> aliasName) throws JSQLParserException {

        // todo 处理exists
        if(StrUtil.isNotBlank(whereSql)&& CollUtil.isNotEmpty(aliasName)){
            for (String alias : aliasName) {
                Expression expression = CCJSqlParserUtil
                        .parseCondExpression(whereSql);
                expression.accept(new ExpressionVisitorAdapter(){
                    @Override
                    public void visit(Column column) {
                        if(Objects.isNull(column.getTable())|| ALIAS_SYNBOL.equals(column.getTable().toString())){
                            Table table = new Table();
                            table.setAlias(new Alias(alias));
                            column.setTable(table);
                        }
                    }
                });
                if(ObjectUtil.isNull(select.getWhere())){
                    select.setWhere(expression);
                }else {
                    AndExpression andExpression = new AndExpression(select.getWhere(), expression);
                    select.setWhere(andExpression);
                }
            }
        }
        return select;
    }


    public class TableConfig{
        private String tableName;
        private Boolean isIgnore;

        public TableConfig(String tableName, Boolean isIgnore) {
            this.tableName = tableName;
            this.isIgnore = isIgnore;
        }

        public String getTableName() {
            return tableName;
        }

        public Boolean getIgnore() {
            return isIgnore;
        }
    }
}
