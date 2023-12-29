package com.example.script;

import com.alibaba.druid.util.JdbcConstants;
import com.example.script.common.expression.Context;
import com.example.script.common.expression.DataTableExpression;
import com.example.script.common.expression.RuleExpression;
import com.example.script.common.expression.RuleInterpreter;
import com.example.script.common.factory.DataSourceFactory;
import com.example.script.common.strategy.DataSourceStrategy;
import com.example.script.constant.DataSourceTypeEnum;
import com.example.script.local.entity.SyncCol;
import com.example.script.local.service.SyncColService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.script.constant.DBConstant.*;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@SpringBootTest
public class DataSourceFactoryTest {

    @Test
    void context() throws SQLException {
        DataSourceFactory dataSourceFactory = DataSourceTypeEnum.toGetFactory(0);
        DataSourceStrategy dataSourceStrategy = dataSourceFactory.getDataSource(JdbcConstants.MYSQL.name());
        dataSourceStrategy.createDataSource(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);
//        DataSourceStrategy dataSourceStrategy2 = dataSourceFactory.getDataSource(JdbcConstants.MYSQL.name());
//        dataSourceStrategy2.createDataSource(TARGET_URL, TARGET_USERNAME, TARGET_PASSWORD);
////        dataSourceStrategy.closeConn();
//        dataSourceStrategy2.closeConn();
//        DataSourceFactory dataSourceFactory1 = DataSourceTypeEnum.toGetFactory(1);
//        DataSourceStrategy dataSourceStrategy1 = dataSourceFactory1.getDataSource(JdbcConstants.MYSQL.name());
//        System.out.println(dataSourceStrategy1);
        Map<String, Map<String, List<String>>> getInitData = dataSourceStrategy.toGetData();
        List<String> allDatabases = dataSourceStrategy.getAllDatabases();
        Map<String, List<String>> tableNames = dataSourceStrategy.getTableNames();
        allDatabases.forEach(System.out::println);
        String catalog = dataSourceStrategy.getCatalog();
        List<String> tableNamesByCatalog = dataSourceStrategy.getTableNamesByCatalog();
        List<String> tableNames1 = dataSourceStrategy.getTableNames("biz-center");
        Map<String, List<Map<String, Object>>> getDataByCatalog = dataSourceStrategy.toGetDataByCatalog();
        tableNames.forEach((key, value)-> System.out.println(key+" >>>>>>>  "+value));
        getInitData.forEach((key, value)-> System.out.println(key+" >>>>>>>  "+value));
    }

    public void format(Map<String, Map<String, List<String>>> getInitData){

    }

    @Autowired
    private SyncColService syncColService;
    @Test
    void testSelect(){
        List<SyncCol> columnStructuresList=new ArrayList<>();
        for (int i=0;i<10;i++){
            SyncCol build = SyncCol.builder()
                                                     .defaultValue("23" + i).size(4112L+i).build();
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
