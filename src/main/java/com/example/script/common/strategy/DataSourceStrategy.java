package com.example.script.common.strategy;

import com.example.script.common.domain.TableData;
import com.example.script.common.domain.TableInfo;
import com.example.script.common.domain.TableKey;

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

    Map<String, Map<String, List<Map<String, Object>>>> toGetData() throws SQLException;

    Map<String, Map<String, Map<Integer, List<TableData>>>> toGetTableData() throws SQLException;

    Map<String, List<Map<String, Object>>> toGetDataByCatalog() throws SQLException;

    List<Map<String, Object>> toGetDataByTable(String databaseName, String tableName) throws SQLException;

    Map<Integer, List<TableData>> toGetTableDataByTable(String databaseName, String tableName) throws SQLException;

    List<String> getAllDatabases() throws SQLException;

    String getCatalog() throws SQLException;

    List<String> getTableNamesByCatalog() throws SQLException;


    List<String> getTableNames(String databaseName) throws SQLException;

    Map<String, List<String>> getTableNames() throws SQLException;

    Map<String, List<Map<String, Object>>> toGetDataByDataBase(String databaseName) throws SQLException;

    Map<String, List<String>> getTableStructure() throws SQLException;

    List<String> getTableStructure(String databaseName) throws SQLException;

    Map<String, Map<String, TableKey>> getAllPrimaryOrUniqueKeys() throws SQLException;

    Map<String, TableKey> getPrimaryOrUniqueKeysByDataBase(String databaseName) throws SQLException;

    Map<String, Map<String, TableInfo>> getAllTableInfo() throws SQLException;

    Map<String, TableInfo> getTableInfo(String databaseName) throws SQLException;
}
