package com.example.demo.web.core.interceptor;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.*;

/**
 * 拦截慢查询并记录日志
 */
@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "query",
        args = {Statement.class, ResultHandler.class}
)})
@Slf4j
public class SlowQueryInteceptor implements Interceptor {

    //慢查询阈值，单位ms
    private long maxTime = 0L;
    private Method druidGetSQLMethod;

    public SlowQueryInteceptor() {
    }

    public SlowQueryInteceptor(long maxTime) {
        this.maxTime = maxTime;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object firstArg = invocation.getArgs()[0];
        Statement statement;
        if (Proxy.isProxyClass(firstArg.getClass())) {
            statement = (Statement) SystemMetaObject.forObject(firstArg).getValue("h.statement");
        } else {
            statement = (Statement) firstArg;
        }

        MetaObject stmtMetaObj = SystemMetaObject.forObject(statement);

        try {
            statement = (Statement) stmtMetaObj.getValue("stmt.statement");
        } catch (Exception var20) {
        }

        if (stmtMetaObj.hasGetter("delegate")) {
            try {
                statement = (Statement) stmtMetaObj.getValue("delegate");
            } catch (Exception var19) {
            }
        }

        String originalSql = null;
        String stmtClassName = statement.getClass().getName();
        Class clazz;
        Object stmtSql;
        if ("com.alibaba.druid.pool.DruidPooledPreparedStatement".equals(stmtClassName)) {
            try {
                if (this.druidGetSQLMethod == null) {
                    clazz = Class.forName("com.alibaba.druid.pool.DruidPooledPreparedStatement");
                    this.druidGetSQLMethod = clazz.getMethod("getSql");
                }
                stmtSql = this.druidGetSQLMethod.invoke(statement);
                if (stmtSql instanceof String) {
                    originalSql = (String) stmtSql;
                }
            } catch (Exception var18) {
                var18.printStackTrace();
            }
        }

        if (originalSql == null) {
            originalSql = statement.toString();
        }

        originalSql = originalSql.replaceAll("[\\s]+", " ");
        int index = this.indexOfSqlStart(originalSql);
        if (index > 0) {
            originalSql = originalSql.substring(index);
        }

        long start = SystemClock.now();
        Object result = invocation.proceed();
        long timing = SystemClock.now() - start;
        Object target = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(target);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        StringBuilder formatSql = (new StringBuilder()).append(" Time：").append(timing).append(" ms - ID：").append(ms.getId()).append("\n").append("Execute SQL：").append(SqlUtils.sqlFormat(originalSql, false)).append("\n");
        if (this.maxTime >= 1L && timing > this.maxTime) {
            log.error(formatSql.toString());
        } else {
            log.debug(formatSql.toString());
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return target instanceof StatementHandler ? Plugin.wrap(target, this) : target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 获取sql语句开头部分
     *
     * @param sql ignore
     * @return ignore
     */
    private int indexOfSqlStart(String sql) {
        String upperCaseSql = sql.toUpperCase();
        Set<Integer> set = new HashSet<>();
        set.add(upperCaseSql.indexOf("SELECT "));
        set.add(upperCaseSql.indexOf("UPDATE "));
        set.add(upperCaseSql.indexOf("INSERT "));
        set.add(upperCaseSql.indexOf("DELETE "));
        set.remove(-1);
        if (CollectionUtils.isEmpty(set)) {
            return -1;
        }
        List<Integer> list = new ArrayList<>(set);
        list.sort(Comparator.naturalOrder());
        return list.get(0);
    }
}
