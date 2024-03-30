package com.example.script.common.rule;

import cn.hutool.core.convert.Convert;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author albert lewis
 * @date 2024/2/26
 */
public class SqlUpdateRule {

    public static String nacosRule(String sql){
        String content=null;
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        SQLStatement sqlStatement = parser.parseStatement(); //SQLAlterTableStatement MySqlInsertStatement
        if (sqlStatement instanceof MySqlInsertStatement insertStatement) {
            String tableName = insertStatement.getTableName().getSimpleName();
            tableName=tableName.replace("`","");
            MySqlExportParameterVisitor visitor = new MySqlExportParameterVisitor();
            insertStatement.accept(visitor);
            List<SQLExpr> columns = insertStatement.getColumns();
            List<Object> columnValues = visitor.getParameters();//List<SQLExpr> columnValues =insertStatement.getValues().getValues()
            //字段名称和值
            Map<String, Object> row = new HashMap<>();
            //key字段名称和值
            Map<String, Object> keyValues = new HashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                SQLExpr columnExpr = columns.get(i);
                Object value = columnValues.get(i);
                String column = columnExpr.toString();
                column = column == null ? null : column.replace("`", "");
                row.put(column, value);
            }
            if ("yaml".equalsIgnoreCase(Convert.toStr(row.get("type")))){
                content = Convert.toStr(row.get("content"));
            }

        }
        return content;
    }
}
