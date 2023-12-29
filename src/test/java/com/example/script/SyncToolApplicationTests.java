package com.example.script;

import com.alibaba.druid.pool.DruidDataSource;
import com.example.script.test.constant.FolderType;
import com.example.script.test.constant.SQLSaveType;
import com.example.script.test.domain.DiffDDL;
import com.example.script.test.utils.DBUtils;
import com.example.script.test.utils.MigrationUtils;
import com.example.script.test.comparator.datasource.DataComparator;
import com.example.script.test.comparator.datasource.TableComparator;
import com.example.script.test.comparator.sqlfile.DataFileComparator;
import com.example.script.test.comparator.sqlfile.TableFileComparator;
import com.example.script.test.utils.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.example.script.test.constant.DBConstant.*;

/**
 * @author albert lewis
 * @date 2023/12/26
 */
@SpringBootTest
public class SyncToolApplicationTests {

    @Test
    void contextTest() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("计时");
        getSQL();
        getSQLBySourceFile();


        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }

    private static void getSQLBySourceFile() {

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

    @Test
    void getCurrentProcess() throws Exception {
        // 获取当前进程ID
        String processId = getProcessId();
        System.out.println("Process ID: " + processId);

        // 获取本地端口和IP地址
        InetSocketAddress socketAddress = getLocalSocketAddress();
        String ipAddress = socketAddress.getAddress().getHostAddress();
        int port = socketAddress.getPort();
        System.out.println("IP Address: " + ipAddress);
        System.out.println("Port: " + port);
        getAllAddress();
        getProcess();
    }

    private static String getProcessId() {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        return processName.split("@")[0];
    }

    private static InetSocketAddress getLocalSocketAddress() {
        try {
            // 创建一个临时Socket以获取本地地址
            InetAddress localhost = InetAddress.getByName("localhost");
            try (java.net.Socket socket = new java.net.Socket()) {
                socket.bind(new InetSocketAddress(localhost, 0));
                return (InetSocketAddress) socket.getLocalSocketAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getAllAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // 跳过回环接口和未启用的接口
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address) {
                        System.out.println("IPv4 Address: " + address.getHostAddress());
                    } else if (address instanceof Inet6Address) {
                        System.out.println("IPv6 Address: " + address.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    void getProcess() throws Exception {
        String targetProcessName = "TargetProcessName"; // 目标进程名称

        try {
            // 执行 jps 命令
            Process process = Runtime.getRuntime().exec("jps -l");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
//                if (line.contains(targetProcessName)) {
                // 解析输出，获取进程 ID
                String[] tokens = line.split(" ");
                String processId = tokens[0];
                String processName = "";
                if (tokens.length > 1) {
                    processName = tokens[1];
                }

                System.out.println("Process ID: " + processId + "    ," + processName);
//                    break;
//                }
            }

            reader.close();
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
