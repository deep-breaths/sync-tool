package com.example.script.common.strategy;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.example.script.constant.DBConstant.DRIVER_CLASS_NAME;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@Getter
public abstract class DataBaseStrategy implements DataSourceStrategy {
    private Connection conn;
    private DataSource dataSource;

    private List<String> databaseNames;
    private Map<String, List<String>> tableNames;
    private Map<String, Map<String, Set<String>>> keys;


    public abstract String getTableStructure(Connection conn, String databaseName, String tableName) throws SQLException;

    protected abstract List<String> generateInsertDataStatements(Connection conn, String databaseName) throws SQLException;

    protected abstract List<String> generateCreateTableStatements(Connection conn, String databaseName) throws SQLException;

    public abstract Map<String, Set<String>> getPrimaryOrUniqueKeys(Connection conn, String databaseName,
                                                                    String tableName) throws SQLException;

    @Override
    public void createDataSource(String url, String username, String password) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        this.dataSource = dataSource;
        this.conn = dataSource.getConnection();
    }

    @Override
    public void closeConn() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }


    @Override
    public List<String> getAllDatabases() throws SQLException {

        return getAllDatabases(conn, null);
    }

    public List<String> getAllDatabases(Connection conn) throws SQLException {
        return getAllDatabases(conn, conn.getCatalog());
    }

    public List<String> getAllDatabases(Connection conn, String catalog) throws SQLException {

        List<String> databases = new ArrayList<>();
        if (catalog != null && !catalog.isEmpty()) {
            databases.add(catalog);
            return databases;
        }
        ResultSet rs = conn.getMetaData().getCatalogs();

        while (rs.next()) {
            databases.add(rs.getString(1));
        }

        rs.close();
        this.databaseNames = databases;
        return databases;
    }

    @Override
    public String getCatalog() throws SQLException {
        return conn.getCatalog();
    }

    @Override
    public List<String> getTableNamesByCatalog() throws SQLException {
        return getTableNames(conn.getCatalog());
    }

    @Override
    public Map<String, List<String>> getTableNames() throws SQLException {

        this.databaseNames = getAllDatabases(conn, null);

        this.tableNames = new HashMap<>();
        for (String databaseName : databaseNames) {
            List<String> getTableNamesByDatabase = getTableNames(databaseName);
            this.tableNames.computeIfAbsent(databaseName, k -> new ArrayList<>()).addAll(getTableNamesByDatabase);
        }

        return this.tableNames;
    }

    @Override
    public List<String> getTableNames(String databaseName) throws SQLException {
        return getTableNames(conn, databaseName);
    }

    protected List<String> getTableNames(Connection conn, String databaseName) throws SQLException {
        List<String> tables = new ArrayList<>();
        /**
         * metadata.getTables(databaseName, null, null, new String[]{"TABLE"}) 用于获取指定数据库中类型为 "TABLE" 的所有表。
         * metadata.getTables(databaseName, null, "%", null) 用于获取指定数据库中的所有表，不限制表类型，并且可以根据表名进行模式匹配筛选。
         */
        ResultSet rs = conn.getMetaData().getTables(databaseName, null, null, new String[]{"TABLE"});

        while (rs.next()) {
            tables.add(rs.getString("TABLE_NAME")); // 获取表名(3)
        }
        rs.close();
        return tables;
    }
}
