package com.example.script.local.web;

import com.alibaba.druid.util.JdbcConstants;
import com.example.script.common.domain.DatabaseInfo;
import com.example.script.common.entity.*;
import com.example.script.common.factory.DataSourceFactory;
import com.example.script.common.strategy.DataSourceStrategy;
import com.example.script.local.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

import static com.example.script.test.constant.DBConstant.*;

/**
 * @author albert lewis
 * @date 2024/1/18
 */
@RestController("/export")
public class ExportController {
    @Autowired
    private SyncDbService syncDbService;
    @Autowired
    private SyncTableService syncTableService;
    @Autowired
    private SyncColService syncColService;
    @Autowired
    private SyncIndexService syncIndexService;
    @Autowired
    private SyncFkService syncFkService;
    @Autowired
    private SyncDataService syncDataService;

    @GetMapping
    public DatabaseInfo saveData() throws SQLException {
        DatabaseInfo dbData = getDbData();
        List<SyncDb> dbList = dbData.getDbList();
        List<SyncTable> tableList= dbData.getTableList();
        List<SyncCol> colList=dbData.getColList();
        List<SyncIndex> indexList=dbData.getIndexList();
        List<SyncFk> fkList=dbData.getFkList();
        List<SyncData> dataList = dbData.getDataList();
        syncDbService.saveBatch(dbList);
        syncTableService.saveBatch(tableList);
        syncColService.saveBatch(colList);
        syncIndexService.saveBatch(indexList);
        syncFkService.saveBatch(fkList);
        syncDataService.saveBatch(dataList);
        return dbData;
    }
    private DatabaseInfo getDbData() throws SQLException {
        DataSourceStrategy dataSourceStrategy = DataSourceFactory.getDataSource(JdbcConstants.MYSQL.name());
        dataSourceStrategy.createDataSource(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);
        return dataSourceStrategy.getInitData();
    }
}
