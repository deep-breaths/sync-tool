package com.example.script.utils.comparator.sqlfile;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.example.script.domain.TableKey;

import java.util.*;

/**
 * @author albert lewis
 * @date 2023/12/20
 */
public class TableFileComparator {

//    public static Map<String, Map<String, List<String>>> getDiffDDL(Connection sourceConn, Connection targetConn) throws SQLException {
//        Map<String, Map<String, List<String>>> result = new HashMap<>();
//        List<String> databases = DBUtils.getAllDatabases(sourceConn);
//        for (String database : databases) {
//            List<String> diffStatements = compareTableSchema(sourceConn, targetConn, database);
//            if (!diffStatements.isEmpty()) {
//                result.put(DIFF_TABLE, Map.of(database, diffStatements));
//            }
//
//        }
//        return result;
//
//    }

//    public static List<String> compareTableSchema(Connection sourceConn, Connection targetConn, String databaseName) throws SQLException {
//        List<String> diffStatements = new ArrayList<>();
//
//        DatabaseMetaData sourceMetaData = sourceConn.getMetaData();
//        DatabaseMetaData targetMetaData = targetConn.getMetaData();
//
//        // 获取源数据库和目标数据库的表列表
//        ResultSet sourceTables = sourceMetaData.getTables(databaseName, null, null, new String[]{"TABLE"});
//        ResultSet targetTables = targetMetaData.getTables(databaseName, null, null, new String[]{"TABLE"});
//
//        while (sourceTables.next()) {
//            String tableName = sourceTables.getString("TABLE_NAME");
//            if (tableExistsInTarget(tableName, targetTables)) {
//                // 表存在于目标数据库，比较表结构
//                String sourceTableDDL = showCreateTable(sourceConn, null, tableName);
//                String targetTableDDL = showCreateTable(targetConn, null, tableName);
//                if (!sourceTableDDL.equals(targetTableDDL)) {
//                    // 生成差异化语句并添加到diffStatements列表中
//                    List<String> alterTableSQL = generateAlterTableSQL(sourceTableDDL, targetTableDDL);
//                    if (!alterTableSQL.isEmpty()) {
//                        diffStatements.addAll(alterTableSQL);
//                    }
//                }
//            } else {
//                // 表不存在于目标数据库，生成创建表的SQL语句并添加到diffStatements列表中
//                String sourceTableDDL = showCreateTable(sourceConn, null, tableName);
//                diffStatements.add(sourceTableDDL);
//            }
//        }
//
//        // 关闭资源
//
//        return diffStatements;
//    }


    /**
     * 根据建表语句查询主键或唯一键
     *
     * @param tables
     * @return
     */
    public static Map<String, Set<String>> getPrimaryOrUniqueKeys(List<String> tables) {
        Map<String, Set<String>> result = new HashMap<>();
        for (String tableDDL : tables) {
            TableKey tableKey = getPrimaryOrUniqueKeys(tableDDL);
            if (tableKey.getTableName() != null && !tableKey.getKeys().isEmpty()) {
                result.put(tableKey.getTableName(), tableKey.getKeys());
            }
        }

        return result;
    }

    private static TableKey getPrimaryOrUniqueKeys(String tableDDL) {
        Set<String> keys = new HashSet<>();
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(tableDDL, DbType.mysql);
        List<SQLStatement> statements = parser.parseStatementList();

        String tableName = null;
        if (!statements.isEmpty()) {
            SQLStatement statement = statements.getFirst();
            if (statement instanceof MySqlCreateTableStatement createTableStatement) {
                tableName = createTableStatement.getTableName();
                MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
                statement.accept(visitor);
                List<MySqlKey> sqlKeys = createTableStatement.getMysqlKeys();
                for (MySqlKey key : sqlKeys) {
                    if (key instanceof MySqlPrimaryKey columns) {
                        columns
                                .getIndexDefinition()
                                .getColumns()
                                .forEach(column -> keys.add(column.getExpr().toString()));
                        break;
                    } else if (key instanceof MySqlUnique columns) {
                        columns
                                .getIndexDefinition()
                                .getColumns()
                                .forEach(column -> keys.add(column.getExpr().toString()));
                        break;
                    }


                }


            }
        }
        TableKey tableKey = new TableKey();
        tableKey.setTableName(tableName);
        tableKey.setKeys(keys);
        return tableKey;
    }

}
