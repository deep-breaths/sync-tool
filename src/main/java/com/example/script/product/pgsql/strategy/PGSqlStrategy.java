package com.example.script.product.pgsql.strategy;


import com.example.script.common.strategy.DataBaseStrategy;
import com.example.script.product.mysql.DBUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

import static com.example.script.constant.SQLSaveType.DDL_CREATE;
import static com.example.script.constant.SQLSaveType.DML_INSERT;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@Component
@Scope("prototype")
public class PGSqlStrategy extends DataBaseStrategy {
    @Override
    public String getName() {
        return "pgsql";
    }

    @Override
    public void createDataSource(String url, String username, String password) throws SQLException {
       super.createDataSource(url,username,password);
    }

    @Override
    public Map<String, Map<String, List<String>>> toGetInitData(Connection sourceConn) throws SQLException {
        Map<String, Map<String, List<String>>> result = new HashMap<>();
        List<String> databases = super.getAllDatabases(sourceConn);
        for (String database : databases) {
            List<String> createTableStatements = generateCreateTableStatements(sourceConn, database);
            List<String> insertDataStatements = generateInsertDataStatements(sourceConn, database);
            if (!createTableStatements.isEmpty()) {
                result.put(DDL_CREATE, Map.of(database, createTableStatements));
            }
            if (!insertDataStatements.isEmpty()) {
                result.put(DML_INSERT, Map.of(database, insertDataStatements));
            }
        }


        return result;
    }



    @Override
    public List<String> getAllDatabases(Connection conn) throws SQLException {

        List<String> databases = new ArrayList<>();
        if (conn.getCatalog() != null && !conn.getCatalog().isEmpty()) {
            databases.add(conn.getCatalog());
            return databases;
        }
        ResultSet rs = conn.getMetaData().getCatalogs();

        while (rs.next()) {
            databases.add(rs.getString(1));
        }

        rs.close();
        return databases;
    }



    @Override
    public Map<String, Set<String>> getPrimaryOrUniqueKeys(Connection conn, String databaseName, String tableName) throws SQLException {
        Map<String, Set<String>> keys = new HashMap<>();
        DatabaseMetaData metaData = conn.getMetaData();

        // 使用databaseName作为参数查询主键
        ResultSet rs = metaData.getPrimaryKeys(databaseName, databaseName, tableName);
        keys.put("Primary", new HashSet<>());
        while (rs.next()) {
            keys.get("Primary").add(rs.getString("COLUMN_NAME"));
        }
        rs.close();

        rs = metaData.getIndexInfo(databaseName, databaseName, tableName, true, true);
        while (rs.next()) {
            String indexName = rs.getString("INDEX_NAME");
            keys.computeIfAbsent(indexName, k -> new HashSet<>()).add(rs.getString("COLUMN_NAME"));
        }
        rs.close();


        return keys;
    }

    @Override
    protected List<String> generateCreateTableStatements(Connection conn, String databaseName) throws SQLException {
        List<String> statements = new ArrayList<>();
        List<String> tableNames = getTableNames(conn, databaseName);
        for (String tableName : tableNames) {
            var tableStructure = getTableStructure(conn, databaseName, tableName);
            if (tableStructure != null) {
                statements.add(tableStructure + ';');
            }
        }

        return statements;

    }

    @Override
    protected List<String> generateInsertDataStatements(Connection conn, String databaseName) throws SQLException {
        List<String> statements = new ArrayList<>();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet resultSet = metaData.getTables(databaseName, null, null, new String[]{"TABLE"});
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            ResultSet dataResultSet = conn.createStatement().executeQuery(String.format("SELECT * FROM %s", tableName));
            ResultSetMetaData resultSetMetaData = dataResultSet.getMetaData();

            StringBuilder columnNamesBuilder = new StringBuilder();
            columnNamesBuilder.append("INSERT INTO ").append("`").append(tableName).append("`").append(" (");

            int columnCount = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNamesBuilder.append("`").append(resultSetMetaData.getColumnName(i)).append("`");
                if (i < columnCount) {
                    columnNamesBuilder.append(",");
                }
            }

            columnNamesBuilder.append(") VALUES (");

            while (dataResultSet.next()) {
                StringBuilder statementBuilder = new StringBuilder();
                statementBuilder.append(columnNamesBuilder);

                for (int i = 1; i <= columnCount; i++) {
                    Object value = dataResultSet.getObject(i);
                    if (value != null) {
                        statementBuilder.append(DBUtils.convertJavaType(value));
                    } else {
                        statementBuilder.append("NULL");
                    }

                    if (i < columnCount) {
                        statementBuilder.append(",");
                    }
                }

                statementBuilder.append(");");
                statements.add(statementBuilder.toString());
            }

            dataResultSet.close();
        }
        resultSet.close();

        return statements;
    }

    @Override
    public String getTableStructure(Connection conn, String databaseName, String tableName) throws SQLException {
        String sql = String.format("SELECT pg_get_tabledef(%s, %s)", tableName, databaseName);
        ResultSet resultSet = conn.createStatement().executeQuery(sql);
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }

}
