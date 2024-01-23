package com.example.script.utils;

/**
 * @author albert lewis
 * @date 2023/12/20
 */

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.example.script.common.rule.ExportDataRule;
import com.example.script.common.rule.RuleUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import static com.example.script.constant.SQLSaveType.*;
import static com.example.script.utils.DBUtils.BIZ_CENTER;
import static com.example.script.utils.DBUtils.USER_CENTER;

public class MigrationUtils {

    public static Map<String, Map<String, List<String>>> getInitSQL(Connection sourceConn) throws SQLException {
        Map<String, Map<String, List<String>>> result = new HashMap<>();
        List<String> databases = DBUtils.getAllDatabases(sourceConn);
        for (String database : databases) {
            List<String> createTableStatements = generateCreateTableStatements(sourceConn, database);
            List<String> insertDataStatements = generateInsertDataStatements(sourceConn, database);
            if (!createTableStatements.isEmpty()) {
                result.computeIfAbsent(DDL_CREATE, key -> new HashMap<>()).put(database, createTableStatements);
            }
            if (!insertDataStatements.isEmpty()) {
                result.computeIfAbsent(DML_INSERT, key -> new HashMap<>()).put(database, insertDataStatements);
            }
        }


        return result;
    }

    public static Map<String, Map<String, List<String>>> getInitSQL(String path) {

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
                statements.add(tableStructure + ';');
            }
        }

        return statements;

    }

    public static String getTableStructure(Connection conn, String databaseName, String tableName) throws SQLException {
        if (!RuleUtils.checkIsExportTableStruct(databaseName, tableName)) {
            return null;
        }
        String sql = String.format("SHOW CREATE TABLE `%s`.`%s`", databaseName, tableName);
        ResultSet resultSet = conn.createStatement().executeQuery(sql);
        if (resultSet.next()) {
            return resultSet.getString(2);
        }
        return null;
    }

    public static List<String> getTableNames(Connection conn, String databaseName) throws SQLException {
        List<String> tables = new ArrayList<>();
        if (!RuleUtils.checkIsExportDB(databaseName)) {
            return tables;
        }
        ResultSet rs = conn.getMetaData().getTables(databaseName, null, "%", null);

        while (rs.next()) {
            String tableName = rs.getString(3);
            if (RuleUtils.checkIsExportTableStruct(databaseName, tableName)) {
                tables.add(tableName); // 获取表名
            }

        }

        rs.close();
        return tables;
    }

    private static List<String> generateInsertDataStatements(Connection conn, String databaseName) throws SQLException {
        List<String> statements = new ArrayList<>();
        if (!RuleUtils.checkThisDbIsExportData(databaseName)) {
            return statements;
        }

        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet resultSet = metaData.getTables(databaseName, null, null, new String[]{"TABLE"});
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            ExportDataRule exportDataRule = RuleUtils.getTableDataCondition(databaseName, tableName);
            if (!exportDataRule.getIncludeData()) {
                continue;
            }
            String where = exportDataRule.getWhere();
            String selectSql = String.format("SELECT * FROM `%s`.`%s`", databaseName, tableName);
            selectSql = RuleUtils.toSetWhere(where, selectSql);

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
        resultSet.close();

        return statements;
    }

    public static void toExecuteSQL(DataSource dataSource, String path, String type) {

        Map<String, Map<String, List<String>>> allSqlList = FileUtils.getFileByPath(path, type);
        if ("init".equalsIgnoreCase(type)) {
            Map<String, List<String>> listMap = Optional
                    .ofNullable(allSqlList.get(DDL_CREATE))
                    .orElse(new LinkedHashMap<>());
            Map<String, List<String>> multiTenantCreate = executeSQL(dataSource, listMap);
            listMap = Optional
                    .ofNullable(allSqlList.get(DML_INSERT))
                    .orElse(new LinkedHashMap<>());
            Map<String, List<String>> multiTenantInsert = executeSQL(dataSource, listMap);

            List<String> multiTenantDatabases = DBUtils.getMultiTenantDatabases(dataSource);
            executeMultiTenantSQL(dataSource, multiTenantCreate, multiTenantDatabases);
            executeMultiTenantSQL(dataSource, multiTenantInsert, multiTenantDatabases);

        } else if ("diff".equalsIgnoreCase(type)) {
            Map<String, List<String>> listMap = Optional
                    .ofNullable(allSqlList.get(DIFF_TABLE))
                    .orElse(new LinkedHashMap<>());
            Map<String, List<String>> tableDiff = executeSQL(dataSource, listMap);
            listMap = Optional
                    .ofNullable(allSqlList.get(DML_INSERT))
                    .orElse(new LinkedHashMap<>());
            Map<String, List<String>> insert = executeSQL(dataSource, listMap);
            listMap = Optional
                    .ofNullable(allSqlList.get(DML_UPDATE))
                    .orElse(new LinkedHashMap<>());
            Map<String, List<String>> update = executeSQL(dataSource, listMap);
//            listMap = Optional
//                    .ofNullable(allSqlList.get(DML_DELETE))
//                    .orElse(new LinkedHashMap<>());
//            executeSQL(dataSource, listMap);

            List<String> multiTenantDatabases = DBUtils.getMultiTenantDatabases(dataSource);
            executeMultiTenantSQL(dataSource, tableDiff, multiTenantDatabases);
            executeMultiTenantSQL(dataSource, insert, multiTenantDatabases);
            executeMultiTenantSQL(dataSource, update, multiTenantDatabases);

        }

    }

    private static void executeMultiTenantSQL(DataSource dataSource, Map<String, List<String>> multiTenantSql,
                                              List<String> multiTenantDatabases) {
        for (String databaseName : multiTenantDatabases) {
            List<String> sqlList =new ArrayList<>();
            if (databaseName.endsWith(BIZ_CENTER)) {
                sqlList = Optional.ofNullable(multiTenantSql.get(BIZ_CENTER)).orElse(new ArrayList<>());
            }else if (databaseName.endsWith(USER_CENTER)){
                sqlList = Optional.ofNullable(multiTenantSql.get(USER_CENTER)).orElse(new ArrayList<>());
            }
            for (String sql : sqlList) {
                executeMultiTenantSQL(dataSource, sql, databaseName);
            }
        }
    }
    private static void executeMultiTenantSQL(DataSource dataSource, String sql,String databaseName) {
        try (Connection conn = dataSource.getConnection()) {
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
            SQLStatement sqlStatement = parser.parseStatement(); //SQLAlterTableStatement MySqlInsertStatement
            if (sqlStatement instanceof MySqlInsertStatement statement) {
                String tableName = statement.getTableName().getSimpleName();
                tableName=tableName.replace("`","");
                if (RuleUtils.isMultiTenantData(databaseName, tableName)) {
                    conn.setCatalog(databaseName);
                    conn.createStatement().execute(sql);
                }
            } else if (sqlStatement instanceof MySqlUpdateStatement statement){
                String tableName = statement.getTableName().getSimpleName();
                tableName=tableName.replace("`","");
                if (RuleUtils.isMultiTenantData(databaseName, tableName)) {
                    conn.setCatalog(databaseName);
                    conn.createStatement().execute(sql);
                }
            }else if (sqlStatement instanceof MySqlDeleteStatement statement){
                String tableName = statement.getTableName().getSimpleName();
                tableName=tableName.replace("`","");
                if (RuleUtils.isMultiTenantData(databaseName, tableName)) {
                    conn.setCatalog(databaseName);
                    conn.createStatement().execute(sql);
                }
            }else{
                conn.setCatalog(databaseName);
                conn.createStatement().execute(sql);
            }

        } catch (SQLException e) {
            System.err.printf("错误租户数据库：%s，错误sql：\n %s \n", databaseName, sql);
            throw new RuntimeException(e);
        }
    }

    private static Map<String, List<String>> executeSQL(DataSource dataSource, Map<String, List<String>> listMap) {
        Map<String, List<String>> multiTenantSQL = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : listMap.entrySet()) {
            String databaseName = entry.getKey();
            List<String> sqlList = entry.getValue();
            System.out.println("更新数据库：" + databaseName);
            if (RuleUtils.isMultiTenantDB(databaseName)) {
                multiTenantSQL.put(databaseName, sqlList);
            }
            for (String sql : sqlList) {
                executeEachSQL(dataSource, sql, databaseName);
            }
        }
        return multiTenantSQL;
    }

    private static void executeEachSQL(DataSource dataSource, String sql, String databaseName) {
        try (Connection conn = dataSource.getConnection()) {
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
            SQLStatement sqlStatement = parser.parseStatement();
            if (sqlStatement instanceof SQLCreateDatabaseStatement) {
                conn.createStatement().execute(sql);
            } else {
                conn.setCatalog(databaseName);
                conn.createStatement().execute(sql);
            }

        } catch (SQLException e) {
            System.err.printf("错误数据库：%s，错误sql：\n %s \n", databaseName, sql);
            throw new RuntimeException(e);
        }
    }


}
