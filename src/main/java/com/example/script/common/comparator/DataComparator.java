package com.example.script.common.comparator;

import com.example.script.common.domain.Database;
import com.example.script.common.domain.TableInfo;
import com.example.script.common.domain.TableStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author albert lewis
 * @date 2023/12/31
 */
public class DataComparator {

    public void comparator(Map<String, Map<String, TableInfo>> sourceTableInfo,
                           Map<String, Map<String, TableInfo>> targetTableInfo){
        List<Database> databases=new ArrayList<>();
        sourceTableInfo.forEach((databaseName,tableInfoMap)->{
            databases.add(comparator(targetTableInfo, databaseName, tableInfoMap));
        });

    }

    private static Database comparator(Map<String, Map<String, TableInfo>> targetTableInfo, String databaseName, Map<String, TableInfo> tableInfoMap) {
        Database database=new Database();
        if (targetTableInfo.get(databaseName)==null){
        database.setName(databaseName);
        database.setStatus(0);
        //todo 将表结构和数据添加
        }else {
            Map<String, TableInfo> targetTableInfoMap = targetTableInfo.get(databaseName);
            List<TableStatus> tableStatuses=new ArrayList<>();
            tableInfoMap.forEach((tableName, sourceTable)->{
                tableStatuses.add(comparator(tableName, sourceTable, targetTableInfoMap));

            });
            database.setTableStatus(tableStatuses);
            database.setName(databaseName);
            database.setStatus(1);
        }

        return database;
    }

    private static TableStatus comparator(String tableName, TableInfo sourceTable, Map<String, TableInfo> targetTableInfoMap) {
        TableStatus tableStatus=new TableStatus();
        if (targetTableInfoMap.get(tableName)==null) {
            tableStatus.setCreate(sourceTable.getTableStatement());
            //todo 将数据添加
        }else {
            TableInfo targetTable = targetTableInfoMap.get(tableName);

            //todo 对比表结构

            //todo 遍历数据并对比


        }

        return tableStatus;
    }
}
