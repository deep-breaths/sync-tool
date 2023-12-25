package com.example.script.utils.comparator.datasource;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.example.script.utils.DBUtils;
import com.example.script.utils.comparator.BuildSQL;

import java.sql.*;
import java.util.*;

import static com.example.script.constant.SQLSaveType.*;

/**
 * @author albert lewis
 * @date 2023/12/21
 */
public class DataComparator {

    public static Map<String, Map<String, List<String>>> getDiffDML(Connection sourceConn, Connection targetConn) throws SQLException {
        Map<String, Map<String, List<String>>> result = new HashMap<>();
        List<String> databases = DBUtils.getAllDatabases(sourceConn);

        for (String database : databases) {
            List<String> tables = DBUtils.getTableNames(sourceConn, database);
            List<String> inserts = new ArrayList<>();
            List<String> updates = new ArrayList<>();
            List<String> deletes = new ArrayList<>();
            for (String table : tables) {
                getDiffDML(sourceConn, targetConn, database, table, inserts, updates, deletes);

            }
            if (!inserts.isEmpty()) {
                result.put(DML_INSERT, Map.of(database, inserts));
            }
            if (!updates.isEmpty()) {
                result.put(DML_UPDATE, Map.of(database, updates));
            }
            if (!deletes.isEmpty()) {
                result.put(DML_DELETE, Map.of(database, deletes));
            }
        }
        return result;

    }

    private static void getDiffDML(Connection sourceConn, Connection targetConn, String database, String table,
                                List<String> inserts, List<String> updates, List<String> deletes) {
        try {
            Set<String> keys = DBUtils.getPrimaryOrUniqueKeys(sourceConn, database, table);

            Map<Map<String, Object>, Map<String, Object>> sourceData = fetchData(sourceConn, database, table, keys);
            Map<Map<String, Object>, Map<String, Object>> targetData = fetchData(targetConn, database, table, keys);


            // 检测变化
            for (Map.Entry<Map<String, Object>, Map<String, Object>> entry : sourceData.entrySet()) {
                if (!targetData.containsKey(entry.getKey())) {
                    inserts.add(BuildSQL.buildInsertSql(database, table, entry.getValue()));
                } else if (!entry.getValue().equals(targetData.get(entry.getKey()))) {
                    updates.add(BuildSQL.buildUpdateSql(database, table, entry.getKey(), entry.getValue()));
                }
            }

            for (Map<String, Object> key : targetData.keySet()) {
                if (!sourceData.containsKey(key)) {
                    deletes.add(BuildSQL.buildDeleteSql(database, table, key));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<Map<String, Object>, Map<String, Object>> fetchData(Connection conn, String databaseName, String tableName, Set<String> keys) throws SQLException {
        Map<Map<String, Object>, Map<String, Object>> data = new HashMap<>();
        Statement stmt = conn.createStatement();
        String sql = String.format("SELECT * FROM `%s`.`%s`;", databaseName, tableName);
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, "mysql");
        SQLStatement sqlStatement = parser.parseStatement();
        ResultSet rs = stmt.executeQuery(sqlStatement.toString());
        ResultSetMetaData metaData = rs.getMetaData();

        while (rs.next()) {
            int numColumns = metaData.getColumnCount();
            Map<String, Object> row = new HashMap<>();
            Map<String, Object> keyValues = new HashMap<>();
            for (int i = 1; i <= numColumns; i++) {
                String column = metaData.getColumnName(i);
                Object value = rs.getObject(column);
                row.put(column, value);
                if (keys.contains(column)) {
                    keyValues.put(column, value);
                }
            }
            data.put(keyValues, row);
        }

        rs.close();
        stmt.close();
        return data;
    }


}
