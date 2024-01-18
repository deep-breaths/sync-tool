package com.example.script;

import com.alibaba.druid.util.JdbcConstants;
import com.example.script.common.domain.DatabaseInfo;
import com.example.script.common.entity.*;
import com.example.script.common.expression.Context;
import com.example.script.common.expression.DataTableExpression;
import com.example.script.common.expression.RuleExpression;
import com.example.script.common.expression.RuleInterpreter;
import com.example.script.common.factory.DataSourceFactory;
import com.example.script.common.strategy.DataSourceStrategy;
import com.example.script.local.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.script.test.constant.DBConstant.*;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@SpringBootTest
public class DataSourceFactoryTest {

    @Test
    void context() throws SQLException {
        StopWatch stopWatch=new StopWatch();
        stopWatch.start("耗时");
        getDbData();
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }

    private static DatabaseInfo getDbData() throws SQLException {
        DataSourceStrategy dataSourceStrategy = DataSourceFactory.getDataSource(JdbcConstants.MYSQL.name());
        dataSourceStrategy.createDataSource(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);
        DatabaseInfo allTableInfo = dataSourceStrategy.getInitData();
        System.out.println(allTableInfo);
        return allTableInfo;
    }

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

    @Test
    void saveData() throws SQLException {
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
    }
    @Test
    void testSelect(){
        List<SyncCol> columnStructuresList=new ArrayList<>();
        for (int i=0;i<10;i++){
            SyncCol build = new SyncCol().setDefaultValue("23" + i).setSize(4112L+i);
            columnStructuresList.add(build);
        }
        syncColService.saveBatch(columnStructuresList);
        List<SyncCol> list = syncColService.list();

        list.forEach(System.out::println);
    }

    @Test
    void test(){
        // 定义备份规则
        Map<String, Map<String, String>> backupRules = new HashMap<>();

        // 添加备份规则
        addBackupRule(backupRules, "dataA", "tableB", "*");
        addBackupRule(backupRules, "dataX", "tableY", "condition");

        // 解析和执行备份规则
        Context context = new Context(backupRules);
        executeBackupRules(context);
    }

    public static void addBackupRule(Map<String, Map<String, String>> backupRules, String dataName, String tableName, String condition) {
        Map<String, String> tableRule = backupRules.computeIfAbsent(dataName, k -> new HashMap<>());
        tableRule.put(tableName, condition);
    }

    public static void executeBackupRules(Context context) {
        Map<String, Map<String, String>> backupRules = context.getBackupRules();

        // 遍历备份规则并执行备份操作
        for (Map.Entry<String, Map<String, String>> entry : backupRules.entrySet()) {
            String dataName = entry.getKey();
            Map<String, String> tableRules = entry.getValue();

            for (Map.Entry<String, String> tableEntry : tableRules.entrySet()) {
                String tableName = tableEntry.getKey();
                String condition = tableEntry.getValue();

                RuleExpression rule = buildRuleExpression(dataName, tableName, condition);
                RuleInterpreter interpreter = new RuleInterpreter(rule);
                interpreter.interpret(context);
            }
        }
    }

    public static RuleExpression buildRuleExpression(String dataName, String tableName, String condition) {
        if (condition.equals("*") || condition.isEmpty()) {
            return new DataTableExpression(tableName, dataName);
        } else {
            // 可根据具体需求进行扩展，针对带条件备份的情况构建相应的表达式
            return new DataTableExpression(tableName, dataName);
        }
    }



}
