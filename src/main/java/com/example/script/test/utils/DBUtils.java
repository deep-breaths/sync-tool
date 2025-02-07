package com.example.script.test.utils;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.script.test.constant.DBConstant.DRIVER_CLASS_NAME;

/**
 * @author albert lewis
 * @date 2023/12/21
 */
public class DBUtils {

    /**
     * 创建数据源
     * @param url
     * @param username
     * @param password
     * @return
     */
    public static DruidDataSource createDataSource(String url, String username, String password) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    /**
     * 根据数据库连接获取数据库列表
     * @param conn
     * @return
     * @throws SQLException
     */
    public static List<String> getAllDatabases(Connection conn) throws SQLException {

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

    /**
     * 根据数据库连接查询表列表
     * @param conn
     * @param databaseName
     * @return
     * @throws SQLException
     */
    public static List<String> getTableNames(Connection conn, String databaseName) throws SQLException {
        List<String> tables = new ArrayList<>();
        ResultSet rs = conn.getMetaData().getTables(databaseName, null, "%", null);

        while (rs.next()) {
            tables.add(rs.getString(3)); // 获取表名
        }
        rs.close();
        return tables;
    }

    /**
     * 根据数据库连接查询主键或唯一键
     * @param conn
     * @param databaseName
     * @param tableName
     * @return
     * @throws SQLException
     */
    public static Set<String> getPrimaryOrUniqueKeys(Connection conn, String databaseName, String tableName) throws SQLException {
        Set<String> keys = new HashSet<>();
        DatabaseMetaData metaData = conn.getMetaData();

        // 使用databaseName作为参数查询主键
        ResultSet rs = metaData.getPrimaryKeys(databaseName, databaseName, tableName);
        while (rs.next()) {
            keys.add(rs.getString("COLUMN_NAME"));
        }
        rs.close();

        // 如果没有主键，尝试获取唯一索引
        if (keys.isEmpty()) {
            rs = metaData.getIndexInfo(databaseName, databaseName, tableName, true, true);
            String firstIndexName = null;
            while (rs.next()) {
                String indexName = rs.getString("INDEX_NAME");
                if (firstIndexName == null) {
                    firstIndexName = indexName;
                } else if (!firstIndexName.equals(indexName)) {
                    break;
                }
                keys.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();
        }

        return keys;
    }



    public static String convertJavaType(Object value) {
        switch (value) {
            case null -> {
                return null;
            }
            case String s -> {
                return STR."'\{value}'";
            }
            case Timestamp timestamp -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                String formattedTimestamp = timestamp.toLocalDateTime().format(formatter);
                return STR."'\{formattedTimestamp}'";
            }
            default -> {
                return value.toString();
            }
        }
    }

    public static boolean tableExistsInTarget(String tableName, ResultSet targetTables) throws SQLException {
        while (targetTables.next()) {
            String targetTableName = targetTables.getString("TABLE_NAME");
            if (tableName.equalsIgnoreCase(targetTableName)) {
                return true;
            }
        }
        return false;
    }

    public static String showCreateTable(Connection conn, String schema, String tableName) throws SQLException {
        String createTableSQL = null;
        String showCreateTableSQL;
        if (schema != null && !schema.isEmpty()) {
            // 构造 SHOW CREATE TABLE 语句
            showCreateTableSQL = "SHOW CREATE TABLE `%s`.`%s`".formatted(schema, tableName);
        } else {
            showCreateTableSQL = "SHOW CREATE TABLE `%s`".formatted(tableName);
        }


        // 执行 SHOW CREATE TABLE 语句，获取结果集
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(showCreateTableSQL);

        // 解析结果集，获取表的创建语句
        if (resultSet.next()) {
            createTableSQL = resultSet.getString("Create Table");
        }

        // 关闭资源
        resultSet.close();
        statement.close();

        return STR."\{createTableSQL};";
    }

}
