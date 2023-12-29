package com.example.script.common.strategy;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.script.constant.DBConstant.DRIVER_CLASS_NAME;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@Getter
public abstract class DataBaseStrategy implements DataSourceStrategy {
    private Connection conn;
    private DataSource dataSource;

    public abstract Map<String, Map<String, List<String>>> toGetInitData() throws SQLException;

    public abstract String getName();

    @Override
    public void createDataSource(String url, String username, String password) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        this.dataSource=dataSource;
        this.conn=dataSource.getConnection();
    }

    @Override
    public void closeConn() throws SQLException {
        if (conn!=null){
            conn.close();
        }
    }

    public List<String> getAllDatabases(Connection conn) throws SQLException {

        List<String> databases = new ArrayList<>();
        if (conn.getCatalog() != null && !conn.getCatalog().isEmpty()) {
            databases.add(conn.getCatalog());
            return databases;
        }
        ResultSet rs = conn.getMetaData().getCatalogs();

        while (rs.next()) {
            databases.add(rs.getString("TABLE_CAT"));
        }

        rs.close();
        return databases;
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
    public abstract String getTableStructure(Connection conn, String databaseName, String tableName) throws SQLException;
    protected abstract List<String> generateInsertDataStatements(Connection conn, String databaseName) throws SQLException;
    protected abstract List<String> generateCreateTableStatements(Connection conn, String databaseName) throws SQLException;
    public abstract Map<String,Set<String>> getPrimaryOrUniqueKeys(Connection conn, String databaseName,
                                                                  String tableName) throws SQLException;
}
