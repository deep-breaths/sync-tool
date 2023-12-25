package test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.script.constant.DBConstant.*;

/**
 * @author albert lewis
 * @date 2023/12/20
 */
public class Diff2Tool {


    public static void main(String[] args) throws SQLException {
        try (DruidDataSource sourceDataSource = createDataSource(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD)) {
            try (DruidDataSource targetDataSource = createDataSource(TARGET_URL, TARGET_USERNAME, TARGET_PASSWORD)) {

                try (
                        Connection sourceConn = sourceDataSource.getConnection();
                        Connection targetConn = targetDataSource.getConnection()
                ) {
                    List<String> diffStatements = compareTableSchema(sourceConn, targetConn);
                    for (String sql : diffStatements) {
                        System.out.println(sql);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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
    public static List<String> compareTableSchema(Connection sourceConn, Connection targetConn) throws SQLException {
        List<String> diffStatements = new ArrayList<>();

        DatabaseMetaData sourceMetaData = sourceConn.getMetaData();
        DatabaseMetaData targetMetaData = targetConn.getMetaData();

        // 获取源数据库和目标数据库的表列表
        ResultSet sourceTables = sourceMetaData.getTables(sourceConn.getCatalog(), null, null, new String[]{"TABLE"});
        ResultSet targetTables = targetMetaData.getTables(targetConn.getCatalog(), null, null, new String[]{"TABLE"});

        while (sourceTables.next()) {
            String tableName = sourceTables.getString("TABLE_NAME");
            if (tableExistsInTarget(tableName, targetTables)) {
                // 表存在于目标数据库，比较表结构
                String sourceTableDDL = showCreateTable(sourceConn, null,tableName);
                String targetTableDDL = showCreateTable(targetConn, null,tableName);
                if (!sourceTableDDL.equals(targetTableDDL)) {
                    // 生成差异化语句并添加到diffStatements列表中
                    String alterTableSQL = generateAlterTableSQL(sourceTableDDL, targetTableDDL);
                    diffStatements.add(alterTableSQL);
                }
            } else {
                // 表不存在于目标数据库，生成创建表的SQL语句并添加到diffStatements列表中
                String sourceTableDDL = showCreateTable(sourceConn,null, tableName);
                diffStatements.add(sourceTableDDL);
            }
        }

        // 关闭资源

        return diffStatements;
    }
    public static String showCreateTable(Connection conn, String schema, String tableName) throws SQLException {
        String createTableSQL = null;
        String showCreateTableSQL;
        if (schema!=null&&schema.length()>0){
            // 构造 SHOW CREATE TABLE 语句
            showCreateTableSQL = "SHOW CREATE TABLE " + schema + "." + tableName;
        }else {
            showCreateTableSQL = "SHOW CREATE TABLE " + tableName;
        }


        // 执行 SHOW CREATE TABLE 语句，获取结果集
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(showCreateTableSQL);

        // 解析结果集，获取表的创建语句
        if (resultSet.next()) {
            createTableSQL = resultSet.getString("Create Table");
        }

        // 关闭资源
        resultSet.close();
        statement.close();

        return createTableSQL;
    }
    private static boolean tableExistsInTarget(String tableName, ResultSet targetTables) throws SQLException {
        while (targetTables.next()) {
            String targetTableName = targetTables.getString("TABLE_NAME");
            if (tableName.equalsIgnoreCase(targetTableName)) {
                return true;
            }
        }
        return false;
    }
    public static String generateAlterTableSQL(String sourceTableDDL, String targetTableDDL) {
        // 解析源表的DDL语句
        MySqlStatementParser sourceParser = new MySqlStatementParser(sourceTableDDL);
        List<SQLStatement> sourceStatementList = sourceParser.parseStatementList();

        // 解析目标表的DDL语句
        MySqlStatementParser targetParser = new MySqlStatementParser(targetTableDDL);
        List<SQLStatement> targetStatementList = targetParser.parseStatementList();

        if (sourceStatementList.size() == 1 && targetStatementList.size() == 1) {
            SQLStatement sourceStatement = sourceStatementList.get(0);
            SQLStatement targetStatement = targetStatementList.get(0);

            if (sourceStatement instanceof SQLCreateTableStatement && targetStatement instanceof SQLCreateTableStatement){
                SQLCreateTableStatement sourceCreateTable = (SQLCreateTableStatement) sourceStatement;
                SQLCreateTableStatement targetCreateTable = (SQLCreateTableStatement) targetStatement;
                // 获取源表和目标表的表名
                String sourceTableName = sourceCreateTable.getTableName();
                String targetTableName = sourceCreateTable.getTableName();
                // 生成ALTER TABLE语句
                StringBuilder alterTableSQL = new StringBuilder();


                // 比较源表和目标表的列定义
                List<SQLColumnDefinition> sourceCreateItems = sourceCreateTable.getColumnDefinitions();
                List<SQLColumnDefinition> targetCreateItems = targetCreateTable.getColumnDefinitions();

                for (SQLColumnDefinition sourceColumn : sourceCreateItems) {
                    boolean columnExists = false;

                    for (SQLColumnDefinition targetColumn : targetCreateItems) {
                        if (sourceColumn.getName().equals(targetColumn.getName())) {
                            columnExists = true;
                            break;
                        }
                    }

                    if (!columnExists) {
                        if (alterTableSQL.length() == 0){
                            alterTableSQL.append("ALTER TABLE ").append(targetTableName).append(" ");
                        }
                        // 添加新列
                        alterTableSQL.append("ADD COLUMN ").append(sourceColumn.toString()).append(",");
                    }

                }

                // 移除源表中不存在的列
                for (SQLColumnDefinition targetColumn : targetCreateItems) {
                    boolean columnExists = false;

                    for (SQLColumnDefinition sourceColumn : sourceCreateItems) {
                        if (targetColumn.getName().equals(sourceColumn.getName())) {
                            columnExists = true;
                            break;
                        }
                    }

                    if (!columnExists) {
                        // 删除不存在的列
                        alterTableSQL.append("DROP COLUMN ").append(targetColumn.getColumnName()).append(",");
                    }
                }

                // 移除末尾的逗号
                if (alterTableSQL.length()>0&&alterTableSQL.charAt(alterTableSQL.length() - 1) == ',') {
                    alterTableSQL.deleteCharAt(alterTableSQL.length() - 1);
                }

                return alterTableSQL.toString();

            }
        }

        return null;
    }
}
