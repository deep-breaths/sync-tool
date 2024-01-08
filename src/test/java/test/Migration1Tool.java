package test;

/**
 * @author albert lewis
 * @date 2023/12/20
 */

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.script.constant.DBConstant.*;

public class Migration1Tool {
    public static void main(String[] args) throws Exception {
        // 数据源配置
        DruidDataSource sourceDataSource = createDataSource(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);
        DruidDataSource targetDataSource = createDataSource(TARGET_URL, TARGET_USERNAME, TARGET_PASSWORD);

        try (
                DruidPooledConnection sourceConnection = sourceDataSource.getConnection();
                DruidPooledConnection targetConnection = targetDataSource.getConnection()
        ) {
            // 生成表结构和数据的创建和插入语句
            generateMigrationScript(sourceConnection, targetConnection, "sys_dept", "sys_dept");
        }
    }
    private static DruidDataSource createDataSource(String url, String username, String password) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
    public static void generateMigrationScript(DruidPooledConnection sourceConnection, DruidPooledConnection targetConnection,
                                               String sourceTableName, String targetTableName) throws Exception {
        // 获取源表结构
        String sourceTableStructure = getTableStructure(sourceConnection, sourceTableName);

        // 获取源表数据
        List<String> sourceTableData = getTableData(sourceConnection, sourceTableName);

        // 生成目标表结构的SQL语句
        String targetTableStructure = generateTableStructureSQL(sourceTableStructure,sourceTableName, targetTableName);

        // 生成目标表数据的SQL语句
        List<String> targetTableData = generateTableDataSQL(sourceTableData,sourceTableName, targetTableName);

        // 输出生成的SQL语句
        System.out.println("Table Structure SQL:\n" + targetTableStructure);
        System.out.println("Table Data SQL:");
        for (String sql : targetTableData) {
            System.out.println(sql);
        }
    }

    public static String getTableStructure(DruidPooledConnection connection, String tableName) throws SQLException {
        String sql = String.format("SHOW CREATE TABLE `%s`",tableName);
        ResultSet resultSet = connection.createStatement().executeQuery(sql);
        if (resultSet.next()) {
            return resultSet.getString(2);
        }
        return null;
    }

    public static List<String> getTableData(DruidPooledConnection connection, String tableName) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        ResultSet resultSet = connection.createStatement().executeQuery(sql);
        return ResultSetUtils.resultSetToList(resultSet);
    }

    public static String generateTableStructureSQL(String sourceStructure,String sourceTableName, String targetTableName) {
        // 将源表结构中的表名替换为目标表名
        return sourceStructure.replaceFirst(String.format("CREATE TABLE `%s`",sourceTableName),
                                            String.format("CREATE TABLE `%s`",targetTableName));
    }

    public static List<String> generateTableDataSQL(List<String> sourceData,String sourceTableName,String targetTableName) {
        List<String> targetDataSQL = new ArrayList<>();
        for (String data : sourceData) {
            // 将源表数据中的表名替换为目标表名
            String targetData = data.replaceFirst( String.format("`%s`",sourceTableName), String.format("`%s`",targetTableName));
            targetDataSQL.add(targetData);
        }
        return targetDataSQL;
    }
}
