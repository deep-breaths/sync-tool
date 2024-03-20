package com.example.script.local.web;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.druid.util.JdbcConstants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.script.common.domain.DatabaseInfo;
import com.example.script.common.entity.*;
import com.example.script.common.factory.DataSourceFactory;
import com.example.script.common.strategy.DataSourceStrategy;
import com.example.script.local.aspect.PermissionAnnotation;
import com.example.script.local.service.*;
import com.example.script.test.domain.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor(); // 创建自定义线程池

    @GetMapping
    @PermissionAnnotation()
    public DatabaseInfo saveData(@RequestParam Long id,@RequestParam String name) throws SQLException {
        Test test=new Test();
        test.setName(name);
        test.setId(id);
        List<SyncCol> syncCols = syncColService.selectWithEmpty();
        System.out.println("syncColService输出");
        syncCols.forEach(System.out::println);
        Object fieldValue = ReflectUtil.getFieldValue(test, "name");
        System.out.println("值: " + fieldValue);
        StopWatch stopWatch=new StopWatch();
        stopWatch.start("计时");
        System.out.println("id="+id+"name="+name);
//        DatabaseInfo dbData = getDbData();
//        List<SyncDb> dbList = dbData.getDbList();
//        List<SyncTable> tableList = dbData.getTableList();
//        List<SyncCol> colList = dbData.getColList();
//        List<SyncIndex> indexList = dbData.getIndexList();
//        List<SyncFk> fkList = dbData.getFkList();
//        List<SyncData> dataList = dbData.getDataList();
//        asyncSave(dbList, tableList, colList, indexList, fkList, dataList);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        syncCols = syncColService.selectWithEmpty();
        System.out.println("syncColService输出");
        syncCols.forEach(System.out::println);
        System.out.println("syncColService插入");
        SyncDb syncDb = new SyncDb();
        syncDb.setId(IdUtil.getSnowflake().nextId());
        syncDb.setVersionId(IdUtil.getSnowflake().nextId());
        syncDb.setSchemaName("111");
        syncDb.setCreateTime(new Date());
        syncDbService.insertData(syncDb);
        syncDb = new SyncDb();
        syncDb.setId(IdUtil.getSnowflake().nextId());
        syncDb.setVersionId(IdUtil.getSnowflake().nextId());
        syncDb.setSchemaName("222");
        syncDb.setCreateTime(new Date());
        syncDbService.insertData1(syncDb);
//        List<SyncDb> list = syncDbService.list(new LambdaQueryWrapper<SyncDb>().le(SyncDb::getCreateTime, new Date()));
//        list.forEach(System.out::println);
        System.out.println("lambda query:" + syncDbService.list(Wrappers
                                                                              .<SyncDb>lambdaQuery().select(SyncDb::getSchemaName).le(SyncDb::getCreateTime, new Date())).getFirst());
        return new DatabaseInfo();
    }

    private void asyncSave(List<SyncDb> dbList, List<SyncTable> tableList, List<SyncCol> colList, List<SyncIndex> indexList, List<SyncFk> fkList, List<SyncData> dataList) {

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> syncDbService.saveBatch(dbList), executor);
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> syncTableService.saveBatch(tableList), executor);
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> syncColService.saveBatch(colList), executor);
        CompletableFuture<Void> future4 = CompletableFuture.runAsync(() -> syncIndexService.saveBatch(indexList), executor);
        CompletableFuture<Void> future5 = CompletableFuture.runAsync(() -> syncFkService.saveBatch(fkList), executor);
        CompletableFuture<Void> future6 = CompletableFuture.runAsync(() -> syncDataService.saveBatch(dataList), executor);
        CompletableFuture<Void> allOf = CompletableFuture.allOf(future1,future2,future3,future4,future5,future6);
        try {
            allOf.get(); // 等待所有的CompletableFuture完成
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("*********Size************");
        System.out.println("dbList: "+dbList.size());
        System.out.println("tableList: "+tableList.size());
        System.out.println("colList: "+colList.size());
        System.out.println("indexList: "+indexList.size());
        System.out.println("fkList: "+fkList.size());
        System.out.println("dataList: "+dataList.size());
        System.out.println("*************************");
//        executor.shutdown(); // 关闭线程池
    }

    private void syncSave(List<SyncDb> dbList, List<SyncTable> tableList, List<SyncCol> colList, List<SyncIndex> indexList, List<SyncFk> fkList, List<SyncData> dataList) {
        syncDbService.saveBatch(dbList);
        syncTableService.saveBatch(tableList);
        syncColService.saveBatch(colList);
        syncIndexService.saveBatch(indexList);
        syncFkService.saveBatch(fkList);
        syncDataService.saveBatch(dataList);
    }

    private DatabaseInfo getDbData() throws SQLException {
        DataSourceStrategy dataSourceStrategy = DataSourceFactory.getDataSource(JdbcConstants.MYSQL.name());
        dataSourceStrategy.createDataSource(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);
        return dataSourceStrategy.getInitData();
    }
}
