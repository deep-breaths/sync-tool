package com.example.script.common.strategy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.example.script.common.domain.*;
import com.example.script.common.entity.*;
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
public abstract class DataSourceStrategy {
    private Connection conn;
    private DataSource dataSource;

//    private List<String> databaseNames;
//    private Map<String, List<String>> tableNames;
//    private Map<String, Map<String, Set<String>>> keys;


    public abstract String getName();

    public abstract String getTableStructure(String databaseName, String tableName) throws SQLException;

    public abstract TableInfo parsingTableStatement(SyncTable syncTable);

    public abstract List<SyncData> getTableData(SyncTable syncTable) throws SQLException;
    
    public void createDataSource(String url, String username, String password) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        this.dataSource = dataSource;
        this.conn = dataSource.getConnection();
    }

   
    public void closeConn() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    public DatabaseInfo getInitData() throws SQLException {
        List<SyncDb> dbList=getAllDatabases();
        DatabaseInfo databaseInfo= getAllTableInfo(dbList);
        List<SyncData> dataList=getAllTableData(databaseInfo.getTableList());
        return databaseInfo.setDataList(dataList);
    }

   
    public List<SyncDb> getAllDatabases() throws SQLException {

        return getDatabase(null);
    }

    public List<SyncDb> getDatabase(String dataBaseName) throws SQLException {
        return getDatabase(conn, dataBaseName);
    }

    public List<SyncDb> getDatabase(Connection conn, String catalog) throws SQLException {

        List<SyncDb> databases = new ArrayList<>();
        if (catalog != null && !catalog.isEmpty()) {
            SyncDb syncDb=new SyncDb()
                    .setId(IdUtil.getSnowflake().nextId())
                    .setVersionId(null)
                    .setSchemaName(catalog)
                    ;
            databases.add(syncDb);
            return databases;
        }
        ResultSet rs = conn.getMetaData().getCatalogs();

        while (rs.next()) {
            String databaseName = rs.getString(1);
            if (RuleUtils.checkIsExportDB(databaseName)) {
                SyncDb syncDb=new SyncDb()
                        .setId(IdUtil.getSnowflake().nextId())
                        .setVersionId(null)
                        .setSchemaName(databaseName)
                        ;
                databases.add(syncDb);
            }

        }

        rs.close();
        return databases;
    }

    public DatabaseInfo getAllTableInfo(List<SyncDb> dbList) throws SQLException {
        DatabaseInfo databaseInfo=new DatabaseInfo();
        List<SyncTable> tableList= new ArrayList<>();
        List<SyncCol> colList=new ArrayList<>();
        List<SyncIndex> indexList=new ArrayList<>();
        List<SyncFk> fkList=new ArrayList<>();
        for (SyncDb syncDb : dbList) {
            DatabaseInfo tableInfo = getTableInfo(syncDb);
            if (tableInfo != null) {
                CollUtil.addAll(tableList,tableInfo.getTableList());
                CollUtil.addAll(colList,tableInfo.getColList());
                CollUtil.addAll(indexList,tableInfo.getIndexList());
                CollUtil.addAll(fkList,tableInfo.getFkList());
            }

        }
        return databaseInfo.setDbList(dbList)
                           .setTableList(tableList)
                           .setColList(colList)
                           .setIndexList(indexList)
                           .setFkList(fkList);
    }

    public DatabaseInfo getTableInfo(SyncDb db) throws SQLException {
        String schemaName = db.getSchemaName();
        if (!RuleUtils.checkIsExportDB(schemaName)) {
            return null;
        }
        List<SyncTable> tableList= new ArrayList<>();
        List<SyncCol> colList=new ArrayList<>();
        List<SyncIndex> indexList=new ArrayList<>();
        List<SyncFk> fkList=new ArrayList<>();
        List<String> tableStructures = getTableStructure(schemaName);
        for (String tableStructure : tableStructures) {
            SyncTable syncTable=new SyncTable()
                    .setId(IdUtil.getSnowflake().nextId())
                    .setDbId(db.getId())
                    .setDbName(schemaName)
                    .setVersionId(db.getVersionId())
                    .setTableStatement(tableStructure);
            TableInfo tableInfo = parsingTableStatement(syncTable);
            if (tableInfo != null) {
                tableList.add(tableInfo.getTableInfo());
                CollUtil.addAll(colList,tableInfo.getColList());
                CollUtil.addAll(indexList,tableInfo.getIndexList());
                CollUtil.addAll(fkList,tableInfo.getFkList());

            }

        }
        return new DatabaseInfo()
                .setTableList(tableList)
                .setColList(colList)
                .setIndexList(indexList)
                .setFkList(fkList);
    }

    public List<String> getTableStructure(String databaseName) throws SQLException {
        List<String> tableStructures = new ArrayList<>();
        List<String> tableNames = getTableNames(databaseName);
        for (String tableName : tableNames) {
            tableStructures.add(getTableStructure(databaseName, tableName));
        }
        return tableStructures;
    }

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
            String tableType = rs.getString("TABLE_TYPE");//获取表类型
            if (RuleUtils.checkIsExportTableStruct(databaseName, tableName)) {
                tables.add(tableName); // 获取表名(3)
            }
        }
        rs.close();
        return tables;
    }


    private List<SyncData> getAllTableData(List<SyncTable> tableList) throws SQLException {
        List<SyncData> dataList=new ArrayList<>();
        for (SyncTable syncTable : tableList) {
            List<SyncData> tableData = getTableData(syncTable);
            CollUtil.addAll(dataList,tableData);
        }
        return dataList;
    }


}
