package com.example.script.product.mysql.comparator.datasource;

import com.example.script.product.mysql.DBUtils;
import com.example.script.product.mysql.comparator.BuildSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.script.constant.SQLSaveType.DIFF_TABLE;

/**
 * @author albert lewis
 * @date 2023/12/20
 */
public class TableComparator {

    public static Map<String, Map<String, List<String>>> getDiffDDL(Connection sourceConn, Connection targetConn) throws SQLException {
        Map<String, Map<String, List<String>>> result = new HashMap<>();
        List<String> databases = DBUtils.getAllDatabases(sourceConn);
        for (String database : databases) {
            List<String> diffStatements = compareTableSchema(sourceConn, targetConn, database);
            if (!diffStatements.isEmpty()) {
                result.put(DIFF_TABLE, Map.of(database, diffStatements));
            }

        }
        return result;

    }

    public static List<String> compareTableSchema(Connection sourceConn, Connection targetConn, String databaseName) throws SQLException {
        List<String> diffStatements = new ArrayList<>();

        DatabaseMetaData sourceMetaData = sourceConn.getMetaData();
        DatabaseMetaData targetMetaData = targetConn.getMetaData();

        // 获取源数据库和目标数据库的表列表
        ResultSet sourceTables = sourceMetaData.getTables(databaseName, null, null, new String[]{"TABLE"});
        ResultSet targetTables = targetMetaData.getTables(databaseName, null, null, new String[]{"TABLE"});

        while (sourceTables.next()) {
            String tableName = sourceTables.getString("TABLE_NAME");
            if (DBUtils.tableExistsInTarget(tableName, targetTables)) {
                // 表存在于目标数据库，比较表结构
                String sourceTableDDL = DBUtils.showCreateTable(sourceConn, null, tableName);
                String targetTableDDL = DBUtils.showCreateTable(targetConn, null, tableName);
                if (!sourceTableDDL.equals(targetTableDDL)) {
                    // 生成差异化语句并添加到diffStatements列表中
                    List<String> alterTableSQL = BuildSQL.generateAlterTableSQL(sourceTableDDL, targetTableDDL);
                    if (!alterTableSQL.isEmpty()) {
                        diffStatements.addAll(alterTableSQL);
                    }
                }
            } else {
                // 表不存在于目标数据库，生成创建表的SQL语句并添加到diffStatements列表中
                String sourceTableDDL = DBUtils.showCreateTable(sourceConn, null, tableName);
                diffStatements.add(sourceTableDDL);
            }
        }

        // 关闭资源

        return diffStatements;
    }



}
