package com.example.script;

import cn.hutool.core.date.StopWatch;
import com.alibaba.druid.pool.DruidDataSource;
import com.example.script.utils.DBUtils;
import com.example.script.utils.FileUtils;
import com.example.script.utils.MigrationUtils;
import com.example.script.utils.comparator.datasource.DataComparator;
import com.example.script.utils.comparator.datasource.TableComparator;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.script.constant.DBConstant.*;

public class ScriptApplication {

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("计时");
        getSQL();
        //getSQLBySourceFile();

        //getRule();


        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));

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

            FileUtils.process(initSQL, FileUtils::saveToFile, "./init/", null);
            System.err.println(message.formatted("初始化SQL结束"));
            System.err.println(message.formatted("差异化DDL开始"));
            Map<String, Map<String, List<String>>> diffDDL = TableComparator.getDiffDDL(sourceConn, targetConn);
            FileUtils.process(diffDDL, FileUtils::saveToFile, "./diff/", null);
            System.err.println(message.formatted("差异化DDL结束"));
            System.err.println(message.formatted("差异化DML开始"));
            Map<String, Map<String, List<String>>> diffDML = DataComparator.getDiffDML(sourceConn, targetConn);
            FileUtils.process(diffDML, FileUtils::saveToFile, "./diff/", null);
            System.err.println(message.formatted("差异化DML结束"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
