package com.maizi.bytetune.common.filter;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FilterDruidSQLPrinter extends StatFilter {

    // 查询后执行，适用于 SELECT 等返回 ResultSet 的语句
    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, com.alibaba.druid.proxy.jdbc.ResultSetProxy resultSet) {
        printSingleLineSqlWithParams(statement); // 打印 SQL 和参数
        super.statementExecuteQueryAfter(statement, sql, resultSet); // 调用父类逻辑
    }

    // 更新后执行，适用于 INSERT / UPDATE / DELETE
    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        printSingleLineSqlWithParams(statement); // 打印 SQL 和参数
        super.statementExecuteUpdateAfter(statement, sql, updateCount); // 调用父类逻辑
    }

    // 批量执行后触发，适用于 executeBatch() 方法
    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        printSingleLineSqlWithParams(statement); // 打印 SQL 和参数
        super.statementExecuteBatchAfter(statement, result); // 调用父类逻辑
    }

    // ✅ 这是最终被调用的通用处理逻辑，不管是 SELECT、UPDATE 还是 BATCH 都会走到这里。
    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean firstResult) {
        printSingleLineSqlWithParams(statement); // 打印 SQL 和参数
        super.statementExecuteAfter(statement, sql, firstResult); // 调用父类逻辑
    }

    /**
     * 打印单行 SQL 并替换占位符参数
     *
     * @param statement 当前执行的 StatementProxy
     */
    private void printSingleLineSqlWithParams(StatementProxy statement) {
        MDC.put("JOB", "[定时扫描DB未上传数据]");
        // 1. 获取最近执行的 SQL 或批量 SQL
        String sql = statement.getLastExecuteSql();
        if (sql == null) {
            sql = statement.getBatchSql();
        }
        if (sql == null) {
            return; // 没有 SQL 则直接返回
        }
        // 2. 压缩 SQL 为单行，去掉多余空格和换行
        String singleLineSql = compressSql(sql);

        // 3. 获取参数并替换占位符 '?'
        singleLineSql = replaceParameters(singleLineSql, statement);

        // 4. 输出到日志
        log.debug(singleLineSql);
        MDC.clear();
    }

    /**
     * 压缩 SQL 为单行字符串
     */
    private String compressSql(String sql) {
        return sql.replaceAll("[\\r\\n]+", " ") // 去掉换行
                .replaceAll("\\s+", " ")     // 压缩多个空格
                .trim();
    }

    /**
     * 替换 SQL 中的 ? 占位符为实际参数值
     */
    private String replaceParameters(String sql, StatementProxy statement) {
        int paramCount = statement.getParametersSize();
        if (paramCount == 0) {
            return sql; // 没有参数
        }

        StringBuilder sb = new StringBuilder();
        int paramIndex = 0;

        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == '?' && paramIndex < paramCount) {
                sb.append(formatParameter(statement.getParameter(paramIndex)));
                paramIndex++;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 格式化参数值为 SQL 可读形式
     */
    private String formatParameter(JdbcParameter parameter) {
        if (parameter == null || parameter.getValue() == null) {
            return "null";
        }

        Object val = parameter.getValue();
        if (val instanceof String) {
            return "'" + val + "'";
        }
        return val.toString();
    }

}