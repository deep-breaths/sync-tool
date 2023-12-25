package com.example.script;

import cn.hutool.core.date.StopWatch;
import com.alibaba.druid.pool.DruidDataSource;
import com.example.script.constant.FolderType;
import com.example.script.constant.SQLSaveType;
import com.example.script.domain.DiffDDL;
import com.example.script.utils.DBUtils;
import com.example.script.utils.FileUtils;
import com.example.script.utils.MigrationUtils;
import com.example.script.utils.comparator.datasource.DataComparator;
import com.example.script.utils.comparator.datasource.TableComparator;
import com.example.script.utils.comparator.sqlfile.DataFileComparator;
import com.example.script.utils.comparator.sqlfile.TableFileComparator;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.example.script.constant.DBConstant.*;

public class ScriptApplication {

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("计时");
        //getSQL();
        getSQLBySourceFile();


        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));

    }
    private static void getSQLBySourceFile(){

        try (DruidDataSource targetDataSource = DBUtils.createDataSource(TARGET_URL, TARGET_USERNAME, TARGET_PASSWORD)) {
            Connection targetConn = targetDataSource.getConnection();


            String message = """
                    **************************
                    **********%s***********
                    **************************""";
            System.err.println(message.formatted("差异化DDL开始"));
            DiffDDL diffDDL = TableFileComparator.getDiffDDL(targetConn);
            Map<String, Map<String, List<String>>> diffSchemas = diffDDL.getDiffSchemas();
            FileUtils.process(diffSchemas, FileUtils::saveToFile, "./diff2/");
            System.err.println(message.formatted("差异化DDL结束"));
            System.err.println(message.formatted("差异化DML开始"));
            Map<String, Map<String, List<String>>> diffDML = DataFileComparator.getDiffDML(diffDDL, targetConn);
            FileUtils.process(diffDML, FileUtils::saveToFile, "./diff2/");
            System.err.println(message.formatted("差异化DML结束"));


        } catch (Exception e) {
        e.printStackTrace();
    }

    }

    private static void getSQLByFile() {
        //《SQL文件类型，《数据库名，sql语句》》
        Map<String, Map<String, List<String>>> initSQL = FileUtils.getFileByDefault(FolderType.INIT);
        //《数据库名，建表语句》
        Map<String, List<String>> creates = initSQL.get(SQLSaveType.DDL_CREATE);
        // 《数据库名，《表名，sql语句》》
        Map<String, Map<String, Set<String>>> allKeys = new HashMap<>();
        creates.forEach((databaseName, tables) -> {
            Map<String, Set<String>> tableKeys = TableFileComparator.getPrimaryOrUniqueKeys(tables);
            allKeys.put(databaseName, tableKeys);

        });
        //《数据库名，插入sql语句》
        Map<String, List<String>> inserts = initSQL.get(SQLSaveType.DML_INSERT);

        inserts.forEach((databaseName, insetSQLs) -> {
            Map<String, Map<Map<String, Object>, Map<String, Object>>> stringMapMap = DataFileComparator.fetchData(insetSQLs, allKeys.get(databaseName));

            stringMapMap.entrySet().forEach(System.out::println);
        });
    }

    private static void getSQL() {
        try (DruidDataSource sourceDataSource = DBUtils.createDataSource(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);
             DruidDataSource targetDataSource = DBUtils.createDataSource(TARGET_URL, TARGET_USERNAME, TARGET_PASSWORD)) {

            Connection sourceConn = sourceDataSource.getConnection();
            Connection targetConn = targetDataSource.getConnection();


            String message = """
                    **************************
                    **********%s***********
                    **************************""";
            System.err.println(message.formatted("初始化SQL开始"));

            Map<String, Map<String, List<String>>> initSQL = MigrationUtils.getInitSQL(sourceConn);

            FileUtils.process(initSQL, FileUtils::saveToFile, "./init/");
            System.err.println(message.formatted("初始化SQL结束"));
            System.err.println(message.formatted("差异化DDL开始"));
            Map<String, Map<String, List<String>>> diffDDL = TableComparator.getDiffDDL(sourceConn, targetConn);
            FileUtils.process(diffDDL, FileUtils::saveToFile, "./diff/");
            System.err.println(message.formatted("差异化DDL结束"));
            System.err.println(message.formatted("差异化DML开始"));
            Map<String, Map<String, List<String>>> diffDML = DataComparator.getDiffDML(sourceConn, targetConn);
            FileUtils.process(diffDML, FileUtils::saveToFile, "./diff/");
            System.err.println(message.formatted("差异化DML结束"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
