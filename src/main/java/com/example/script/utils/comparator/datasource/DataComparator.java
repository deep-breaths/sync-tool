package com.example.script.utils.comparator.datasource;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.example.script.common.rule.ExportDataRule;
import com.example.script.common.rule.RuleUtils;
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
                result.computeIfAbsent(DML_INSERT,key->new HashMap<>()).put(database, inserts);
            }
            if (!updates.isEmpty()) {
                result.computeIfAbsent(DML_UPDATE,key->new HashMap<>()).put(database, updates);
            }
            if (!deletes.isEmpty()) {
                result.computeIfAbsent(DML_DELETE,key->new HashMap<>()).put(database, deletes);
            }
        }
        return result;

    }

    private static void getDiffDML(Connection sourceConn, Connection targetConn, String databaseName, String tableName,
                                   List<String> inserts, List<String> updates, List<String> deletes) {
        try {
            Map<String, Set<String>> sourceKeys = DBUtils.getPrimaryOrUniqueKeys(sourceConn, databaseName, tableName);
            Map<String, Set<String>> targetKeys = DBUtils.getPrimaryOrUniqueKeys(targetConn, databaseName, tableName);

            Set<String> keys = null;
            for (Map.Entry<String, Set<String>> sourceKey : sourceKeys.entrySet()) {
                Set<String> value = sourceKey.getValue();
                if (targetKeys.containsValue(value)) {
                    keys = value;
                    break;
                }
            }
            keys = keys == null ? sourceKeys
                    .entrySet()
                    .stream()
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .orElse(null) : keys;

            Map<Map<String, Object>, Map<String, Object>> sourceData = fetchData(sourceConn, databaseName, tableName, keys);
            Map<Map<String, Object>, Map<String, Object>> targetData = fetchData(targetConn, databaseName, tableName, keys);


            // 检测变化
            for (Map.Entry<Map<String, Object>, Map<String, Object>> entry : sourceData.entrySet()) {
                if (!targetData.containsKey(entry.getKey())) {
                    inserts.add(BuildSQL.buildInsertSql(null, tableName, entry.getValue()));
                } else if (!entry.getValue().equals(targetData.get(entry.getKey()))) {
                    updates.add(BuildSQL.buildUpdateSql(null, tableName, entry.getKey(), entry.getValue()));
                }
            }

            for (Map<String, Object> key : targetData.keySet()) {
                if (!sourceData.containsKey(key)) {
                    deletes.add(BuildSQL.buildDeleteSql(null, tableName, key));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<Map<String, Object>, Map<String, Object>> fetchData(Connection conn, String databaseName,
                                                                          String tableName, Set<String> keys) throws SQLException {
        Map<Map<String, Object>, Map<String, Object>> data = new HashMap<>();
        Statement stmt = conn.createStatement();
        String sql = String.format("SELECT * FROM `%s`.`%s`;", databaseName, tableName);

        ExportDataRule exportDataRule = RuleUtils.getTableDataCondition(databaseName, tableName);
        if (!exportDataRule.getIncludeData()) {
            return data;
        }
        String where = exportDataRule.getWhere();
        sql = RuleUtils.toSetWhere(where, sql);


        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        SQLStatement sqlStatement = parser.parseStatement();
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sqlStatement.toString());
        } catch (SQLSyntaxErrorException e) {
            if (e.getMessage().startsWith("Unknown database") || (e.getMessage().startsWith("Table") && e
                    .getMessage()
                    .endsWith("doesn't exist"))) {
                return data;
            }else {
                throw e;
            }
        }
        if (rs == null) {
            return data;
        }
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


    public static Map<String, Map<Map<String, Object>, Map<String, Object>>> fetchData(Connection conn, String databaseName,
                                                                                       List<String> tables,
                                                                                       Map<String, Set<String>> theKeys) throws SQLException {
        Map<String, Map<Map<String, Object>, Map<String, Object>>> data = new HashMap<>();
        for (String tableName : tables) {
            Statement stmt = conn.createStatement();
            Set<String> keys = theKeys.get(tableName);
            String format;
            if (tableName.startsWith("`") && tableName.endsWith("`")) {
                format = "SELECT * FROM `%s`.%s;";
            } else {
                format = "SELECT * FROM `%s`.`%s`;";
            }

            String sql = String.format(format, databaseName, tableName);
            ExportDataRule exportDataRule = RuleUtils.getTableDataCondition(databaseName, tableName);
            if (!exportDataRule.getIncludeData()) {
                continue;
            }
            String where = exportDataRule.getWhere();
            sql = RuleUtils.toSetWhere(where, sql);
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
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
                data.computeIfAbsent(tableName, k -> new HashMap<>());
                data.get(tableName).put(keyValues, row);
            }

            rs.close();
            stmt.close();
        }

        return data;
    }

}
