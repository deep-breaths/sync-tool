package com.example.script.utils;

/**
 * @author albert lewis
 * @date 2023/12/20
 */

import com.example.script.common.rule.ExportDataRule;
import com.example.script.common.rule.RuleUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.script.constant.SQLSaveType.DDL_CREATE;
import static com.example.script.constant.SQLSaveType.DML_INSERT;

public class MigrationUtils {

    public static Map<String,Map<String,List<String>>> getInitSQL(Connection sourceConn) throws SQLException {
        Map<String,Map<String,List<String>>> result = new HashMap<>();
        List<String> databases = DBUtils.getAllDatabases(sourceConn);
        for (String database : databases) {
            List<String> createTableStatements = generateCreateTableStatements(sourceConn, database);
            List<String> insertDataStatements = generateInsertDataStatements(sourceConn, database);
            if (!createTableStatements.isEmpty()) {
                result.computeIfAbsent(DDL_CREATE,key->new HashMap<>()).put(database, createTableStatements);
            }
            if (!insertDataStatements.isEmpty()) {
                result.computeIfAbsent(DML_INSERT, key->new HashMap<>()).put(database, insertDataStatements);
            }
        }


        return result;
    }

    public static Map<String,Map<String,List<String>>> getInitSQL(String path){

        return FileUtils.getInit(path);
    }


    private static List<String> generateCreateTableStatements(Connection conn, String databaseName) throws SQLException {
        List<String> statements = new ArrayList<>();
        if (RuleUtils.checkIsExportDB(databaseName)) {
            String createStatement = String.format("CREATE DATABASE IF NOT EXISTS `%s` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;", databaseName);
            statements.add(createStatement);
        }

        List<String> tableNames = getTableNames(conn, databaseName);
        for (String tableName : tableNames) {
            var tableStructure = getTableStructure(conn, databaseName, tableName);
            if (tableStructure != null) {
                statements.add(tableStructure+ ';');
            }
        }

        return statements;

    }

    public static String getTableStructure(Connection conn, String databaseName, String tableName) throws SQLException {
        if (!RuleUtils.checkIsExportTableStruct(databaseName,tableName)){
            return null;
        }
        String sql = String.format(" SHOW CREATE TABLE `%s`.`%s`", databaseName, tableName);
        ResultSet resultSet = conn.createStatement().executeQuery(sql);
        if (resultSet.next()) {
            return resultSet.getString(2);
        }
        return null;
    }

    public static List<String> getTableNames(Connection conn, String databaseName) throws SQLException {
        List<String> tables = new ArrayList<>();
        if (!RuleUtils.checkIsExportDB(databaseName)){
            return tables;
        }
        ResultSet rs = conn.getMetaData().getTables(databaseName, null, "%", null);

        while (rs.next()) {
            String tableName = rs.getString(3);
            if (RuleUtils.checkIsExportTableStruct(databaseName,tableName)){
                tables.add(tableName); // 获取表名
            }

        }

        rs.close();
        return tables;
    }

    private static List<String> generateInsertDataStatements(Connection conn, String databaseName) throws SQLException {
        List<String> statements = new ArrayList<>();
        if (!RuleUtils.checkIsExportDB(databaseName)){
            return statements;
        }

        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet resultSet = metaData.getTables(databaseName, null, null, new String[]{"TABLE"});
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            ExportDataRule exportDataRule = RuleUtils.getTableDataCondition(databaseName, tableName);
            if (!exportDataRule.getIncludeData()){
                continue;
            }
            String where = exportDataRule.getWhere();
            String selectSql = String.format("SELECT * FROM `%s`.`%s`",databaseName,tableName);
            selectSql=RuleUtils.toSetWhere(where,selectSql);

            ResultSet dataResultSet = conn.createStatement().executeQuery(selectSql);
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
//        while (resultSet.next()) {
//            String tableName = resultSet.getString("TABLE_NAME");
//            ResultSet dataResultSet = conn.createStatement().executeQuery(String.format("SELECT * FROM %s", tableName));
//
//            while (dataResultSet.next()) {
//                StringBuilder statementBuilder = new StringBuilder();
//                statementBuilder.append("INSERT INTO ").append(tableName).append(" VALUES (");
//
//                int columnCount = dataResultSet.getMetaData().getColumnCount();
//                for (int i = 1; i <= columnCount; i++) {
//                    Object value = dataResultSet.getObject(i);
//                    if (value != null) {
//                        statementBuilder.append(DBUtils.convertType(value));
//                    } else {
//                        statementBuilder.append("NULL");
//                    }
//
//                    if (i < columnCount) {
//                        statementBuilder.append(",");
//                    }
//                }
//
//                statementBuilder.append(");");
//                statements.add(statementBuilder.toString());
//            }
//
//            dataResultSet.close();
//        }

        resultSet.close();

        return statements;
    }

}
