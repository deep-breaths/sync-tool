package com.example.script.common.strategy;

import com.alibaba.druid.pool.DruidDataSource;
import com.example.script.common.domain.TableInfo;
import com.example.script.common.domain.TableKey;
import com.example.script.common.rule.RuleUtils;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.example.script.test.constant.DBConstant.DRIVER_CLASS_NAME;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@Getter
public abstract class DataBaseStrategy implements DataSourceStrategy {
    private Connection conn;
    private DataSource dataSource;

//    private List<String> databaseNames;
//    private Map<String, List<String>> tableNames;
//    private Map<String, Map<String, Set<String>>> keys;


    public abstract String getTableStructure(String databaseName, String tableName) throws SQLException;

    public abstract TableKey getPrimaryOrUniqueKeys(String tableDDL);

    public abstract TableInfo parsingTableStatement(String tableDDL);

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

        return getAllDatabases(null);
    }

    public List<String> getAllDatabases(String dataBaseName) throws SQLException {
        return getAllDatabases(conn, dataBaseName);
    }

    public List<String> getAllDatabases(Connection conn, String catalog) throws SQLException {

        List<String> databases = new ArrayList<>();
        if (catalog != null && !catalog.isEmpty()) {
            databases.add(catalog);
            return databases;
        }
        ResultSet rs = conn.getMetaData().getCatalogs();

        while (rs.next()) {
            String databaseName = rs.getString(1);
            if (RuleUtils.checkIsExportDB(databaseName)) {
                databases.add(databaseName);
            }

        }

        rs.close();
        return databases;
    }

    @Override
    public String getCatalog() throws SQLException {
        return conn.getCatalog();
    }

    @Override
    public List<String> getTableNamesByCatalog() throws SQLException {
        return getTableNames(getCatalog());
    }

    @Override
    public Map<String, List<String>> getTableNames() throws SQLException {

        List<String> allDatabases = getAllDatabases(null);

        Map<String, List<String>> tableNames = new LinkedHashMap<>();
        for (String databaseName : allDatabases) {
            List<String> getTableNamesByDatabase = getTableNames(databaseName);
            tableNames.computeIfAbsent(databaseName, k -> new ArrayList<>()).addAll(getTableNamesByDatabase);
        }

        return tableNames;
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
            String tableName = rs.getString("TABLE_NAME");
            if (RuleUtils.checkIsExportTableStruct(databaseName, tableName)) {
                tables.add(tableName); // 获取表名(3)
            }
        }
        rs.close();
        return tables;
    }

    @Override
    public Map<String, Map<String, List<Map<String, Object>>>> toGetData() throws SQLException {
        List<String> allDatabases = getAllDatabases();
        Map<String, Map<String, List<Map<String, Object>>>> result = new LinkedHashMap<>();
        for (String databaseName : allDatabases) {
            Map<String, List<Map<String, Object>>> getData = toGetDataByDataBase(databaseName);
            if (getData!=null&&!getData.isEmpty()){
                result.put(databaseName, getData);
            }else if (RuleUtils.checkThisDbIsExportData(databaseName)){
                result.put(databaseName, new HashMap<>());
            }

        }

        return result;
    }

    @Override
    public Map<String, List<Map<String, Object>>> toGetDataByDataBase(String databaseName) throws SQLException {
        List<String> tableNames = getTableNames(databaseName);
        Map<String, List<Map<String, Object>>> allTableData = new LinkedHashMap<>();
        for (String tableName : tableNames) {
            List<Map<String, Object>> rows = toGetDataByTable(databaseName, tableName);
            if (rows != null && !rows.isEmpty()) {
                allTableData.put(tableName, rows);
            }


        }
        return allTableData;
    }

    @Override
    public Map<String, List<Map<String, Object>>> toGetDataByCatalog() throws SQLException {

        return toGetDataByDataBase(getCatalog());
    }

    @Override
    public Map<String, List<String>> getTableStructure() throws SQLException {
        Map<String, List<String>> tableStructures = new LinkedHashMap<>();
        List<String> allDatabases = getAllDatabases();
        for (String databaseName : allDatabases) {
            tableStructures.put(databaseName, getTableStructure(databaseName));
        }
        return tableStructures;
    }

    @Override
    public List<String> getTableStructure(String databaseName) throws SQLException {
        List<String> tableStructures = new ArrayList<>();
        List<String> tableNames = getTableNames(databaseName);
        for (String tableName : tableNames) {
            tableStructures.add(getTableStructure(databaseName, tableName));
        }
        return tableStructures;
    }

    @Override
    public Map<String, Map<String, TableKey>> getAllPrimaryOrUniqueKeys() throws SQLException {
        Map<String, Map<String, TableKey>> result = new LinkedHashMap<>();
        List<String> allDatabases = getAllDatabases();
        for (String databaseName : allDatabases) {
            Map<String, TableKey> tableKeys = getPrimaryOrUniqueKeysByDataBase(databaseName);
            result.put(databaseName, tableKeys);
        }

        return result;
    }

    @Override
    public Map<String, TableKey> getPrimaryOrUniqueKeysByDataBase(String databaseName) throws SQLException {
        Map<String, TableKey> tableKeyMap = new LinkedHashMap<>();
        List<String> tableStructures = getTableStructure(databaseName);
        for (String tableStructure : tableStructures) {
            TableKey keys = getPrimaryOrUniqueKeys(tableStructure);
            tableKeyMap.put(keys.getTableName(), keys);
        }
        return tableKeyMap;
    }

    @Override
    public Map<String, Map<String, TableInfo>> getAllTableInfo() throws SQLException {
        Map<String, Map<String, TableInfo>> result = new LinkedHashMap<>();
        List<String> allDatabases = getAllDatabases();
        for (String databaseName : allDatabases) {
            Map<String, TableInfo> tableMap = getTableInfo(databaseName);
            if (tableMap != null && !tableMap.isEmpty()) {
                result.put(databaseName, tableMap);
            }

        }

        return result;
    }

    @Override
    public Map<String, TableInfo> getTableInfo(String databaseName) throws SQLException {
        if (!RuleUtils.checkIsExportDB(databaseName)) {
            return new LinkedHashMap<>();
        }
        Map<String, TableInfo> tableMap = new LinkedHashMap<>();
        List<String> tableStructures = getTableStructure(databaseName);
        for (String tableStructure : tableStructures) {
            TableInfo tableInfo = parsingTableStatement(tableStructure);
            if (tableInfo != null) {
                tableMap.put(tableInfo.getName(), tableInfo);
            }

        }
        return tableMap;
    }
}
