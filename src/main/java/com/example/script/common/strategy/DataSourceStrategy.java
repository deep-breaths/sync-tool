package com.example.script.common.strategy;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
public interface DataSourceStrategy {

    String getName();
    void createDataSource(String url, String username, String password) throws SQLException;

    void closeConn() throws SQLException;
    Map<String, Map<String, List<String>>> toGetData() throws SQLException;
    Map<String, List<Map<String,Object>>> toGetDataByCatalog() throws SQLException;
    List<Map<String, Object>> toGetDataByTable(String tableName) throws SQLException;

    List<String> getAllDatabases() throws SQLException;
    String getCatalog() throws SQLException;
    List<String> getTableNamesByCatalog() throws SQLException;



    List<String> getTableNames(String databaseName) throws SQLException;
    Map<String,List<String>> getTableNames() throws SQLException;
}
