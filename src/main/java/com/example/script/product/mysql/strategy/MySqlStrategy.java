package com.example.script.product.mysql.strategy;

import com.alibaba.druid.sql.ast.SQLIndexDefinition;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.example.script.common.domain.Key;
import com.example.script.common.domain.TableKey;
import com.example.script.common.strategy.DataBaseStrategy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@Component
@Scope("prototype")
public class MySqlStrategy extends DataBaseStrategy {
    @Override
    public String getName() {
        return "mysql";
    }





    @Override
    public List<Map<String, Object>> toGetDataByTable(String databaseName, String tableName) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSet dataResultSet = super
                .getConn()
                .createStatement()
                .executeQuery(String.format("SELECT * FROM `%s`.`%s`",databaseName, tableName));
        ResultSetMetaData resultSetMetaData = dataResultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        while (dataResultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(resultSetMetaData.getColumnName(i), dataResultSet.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }



    @Override
    public String getTableStructure(String databaseName, String tableName) throws SQLException {
        String sql = String.format(" SHOW CREATE TABLE `%s`.`%s`", databaseName, tableName);
        ResultSet resultSet = getConn().createStatement().executeQuery(sql);
        if (resultSet.next()) {
            return resultSet.getString(2);
        }
        resultSet.close();
        return null;
    }

    @Override
    public TableKey getPrimaryOrUniqueKeys(String tableDDL) {
        Set<Key> keys = new HashSet<>();
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(tableDDL, JdbcConstants.MYSQL);
        List<SQLStatement> statements = parser.parseStatementList();

        String tableName = null;
        if (!statements.isEmpty()) {
            SQLStatement statement = statements.getFirst();
            if (statement instanceof MySqlCreateTableStatement createTableStatement) {
                tableName = createTableStatement.getTableName();
                tableName=tableName==null?null:tableName.replace("`","");
                MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
                statement.accept(visitor);
                List<MySqlKey> sqlKeys = createTableStatement.getMysqlKeys();
                for (MySqlKey key : sqlKeys) {
                    Key theKey=new Key();
                    SQLIndexDefinition indexDefinition = key
                            .getIndexDefinition();
                    Set<String> list = indexDefinition
                            .getColumns()
                            .stream().map(x -> x.getExpr().toString().replace("`", ""))
                            .collect(Collectors.toSet());

                    if (key instanceof MySqlPrimaryKey) {
                        theKey.setIsOnly(true);

                    } else if (key instanceof MySqlUnique) {
                        theKey.setIsOnly(true);
                    }else {
                        theKey.setIsOnly(false);
                    }
                    theKey.setType(indexDefinition.getType());
                    SQLName name = indexDefinition.getName();
                    theKey.setName(name==null?null:name.toString().replace("`", ""));
                    theKey.setColumns(list);
                    keys.add(theKey);


                }


            }
        }
        TableKey tableKey = new TableKey();
        tableKey.setTableName(tableName);
        tableKey.setKeys(keys);
        return tableKey;
    }

}
