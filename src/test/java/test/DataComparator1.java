package test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author albert lewis
 * @date 2023/12/21
 */
public class DataComparator1 {
    public static void main(String[] args) {
        // 连接源数据库和目标数据库
        String sourceUrl = "jdbc:mysql://source_host/source_db";
        String sourceUsername = "source_user";
        String sourcePassword = "source_password";

        String targetUrl = "jdbc:mysql://target_host/target_db";
        String targetUsername = "target_user";
        String targetPassword = "target_password";

        try (
                Connection sourceConn = DriverManager.getConnection(sourceUrl, sourceUsername, sourcePassword);
                Connection targetConn = DriverManager.getConnection(targetUrl, targetUsername, targetPassword)
        ) {
            // 获取源数据库和目标数据库的表名列表
            List<String> sourceTables = getTableNames(sourceConn);
            List<String> targetTables = getTableNames(targetConn);

            // 遍历每个表
            for (String sourceTable : sourceTables) {
                if (targetTables.contains(sourceTable)) {
                    // 获取源数据库和目标数据库的数据
                    List<String> sourceData = getTableData(sourceConn, sourceTable);
                    List<String> targetData = getTableData(targetConn, sourceTable);

                    // 比较源数据库和目标数据库的数据，并生成差异化的SQL语句
                    for (String sourceRow : sourceData) {
                        boolean matched = false;
                        for (String targetRow : targetData) {
                            if (isSameData(sourceRow, targetRow)) {
                                matched = true;
                                break;
                            }
                        }
                        if (!matched) {
                            // 生成插入语句
                            String insertSql = generateInsertSql(sourceTable, sourceRow);
                            System.out.println(insertSql);
                        }
                    }

                    // 检查是否有需要删除的数据
                    for (String targetRow : targetData) {
                        boolean matched = false;
                        for (String sourceRow : sourceData) {
                            if (isSameData(sourceRow, targetRow)) {
                                matched = true;
                                break;
                            }
                        }
                        if (!matched) {
                            // 生成删除语句
                            String deleteSql = generateDeleteSql(sourceTable, targetRow);
                            System.out.println(deleteSql);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getTableNames(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(connection.getCatalog(), null, null, new String[]{"TABLE"});
        List<String> tableNames = new ArrayList<>();
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            tableNames.add(tableName);
        }
        return tableNames;
    }

    private static List<String> getTableData(Connection connection, String tableName) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            List<String> data = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    row.append(value).append(",");
                }
                data.add(row.toString());
            }
            return data;
        }
    }

    private static boolean isSameData(String sourceRow, String targetRow) {
        // 根据主键或唯一索引判断是否是同一条数据，你可以根据实际情况修改此处的逻辑
        // 此处假设主键是第一个字段，以逗号分隔字段值
        String[] sourceValues = sourceRow.split(",");
        String[] targetValues = targetRow.split(",");
        return sourceValues[0].equals(targetValues[0]);
    }

    private static String generateInsertSql(String tableName, String rowData) {
        // 生成插入语句，你可以根据实际情况修改此处的逻辑
        String[] values = rowData.split(",");
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO ").append(tableName).append(" VALUES (");
        for (String value : values) {
            sqlBuilder.append(value).append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }
    private static String generateUpdateSql(String tableName, String rowData) {
        // 生成更新语句，你可以根据实际情况修改此处的逻辑
        // 此处假设主键是第一个字段，以逗号分隔字段值
        String[] values = rowData.split(",");
        String primaryKeyValue = values[0];

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("UPDATE ").append(tableName).append(" SET ");
        for (int i = 1; i < values.length; i++) {
            String columnValue = values[i];
            // 此处假设列名是按顺序从第一个字段开始的，以逗号分隔
            String columnName = "column" + i;
            sqlBuilder.append(columnName).append(" = ").append(columnValue).append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(" WHERE primary_key = ").append(primaryKeyValue);
        return sqlBuilder.toString();
    }

    private static String generateDeleteSql(String tableName, String rowData) {
        // 生成删除语句，你可以根据实际情况修改此处的逻辑
        // 此处假设主键是第一个字段，以逗号分隔字段值
        String[] values = rowData.split(",");
        String primaryKeyValue = values[0];
        return "DELETE FROM " + tableName + " WHERE primary_key = " + primaryKeyValue;
    }

}
