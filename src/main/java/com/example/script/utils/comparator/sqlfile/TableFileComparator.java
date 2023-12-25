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
import com.example.script.constant.SQLSaveType;
import com.example.script.domain.DiffDDL;
import com.example.script.domain.TableKey;
import com.example.script.utils.DBUtils;
import com.example.script.utils.FileUtils;
import com.example.script.utils.comparator.BuildSQL;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.example.script.constant.SQLSaveType.DIFF_TABLE;

/**
 * @author albert lewis
 * @date 2023/12/20
 */
public class TableFileComparator {

    public static DiffDDL getDiffDDL(Connection targetConn) {
        Map<String, Map<String, List<String>>> result = new HashMap<>();
        Map<String, Map<String, Set<String>>> allKeys = new HashMap<>();
        Map<String, List<String>> creates= getCreatesByDefault();
        creates.forEach((databaseName, tables) -> {
            try {
                List<String> diffStatements = compareTableSchema(tables, targetConn, databaseName);
                if (!diffStatements.isEmpty()) {
                    result.put(DIFF_TABLE, Map.of(databaseName, diffStatements));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Map<String, Set<String>> tableKeys = TableFileComparator.getPrimaryOrUniqueKeys(tables);
            allKeys.put(databaseName, tableKeys);

        });

        DiffDDL diffDDL=new DiffDDL();
        diffDDL.setDiffSchemas(result);
        diffDDL.setKeys(allKeys);
        return diffDDL;

    }

    public static List<String> compareTableSchema(List<String> sourceTables, Connection targetConn, String databaseName) throws SQLException {
        List<String> diffStatements = new ArrayList<>();


        DatabaseMetaData targetMetaData = targetConn.getMetaData();

        // 获取源数据库和目标数据库的表列表
        ResultSet targetTables = targetMetaData.getTables(databaseName, null, null, new String[]{"TABLE"});

        for (String sourceTableDDL : sourceTables) {
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sourceTableDDL, DbType.mysql);
            List<SQLStatement> statements = parser.parseStatementList();
            String tableName = null;
            if (!statements.isEmpty()) {
                SQLStatement statement = statements.getFirst();
                if (statement instanceof MySqlCreateTableStatement createTableStatement) {
                    tableName = createTableStatement.getTableName();
                    if (DBUtils.tableExistsInTarget(tableName.replace("`",""), targetTables)) {
                        // 表存在于目标数据库，比较表结构
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
                        diffStatements.add(sourceTableDDL);
                    }


                }
            }
        }

        return diffStatements;
    }


    /**
     *
     * @return 《数据库名，建表语句》
     */
    public static Map<String, List<String>> getCreatesByDefault() {
        return FileUtils.getInitSQLByDefault().get(SQLSaveType.DDL_CREATE);
    }

    /**
     *
     * @return 《SQL文件类型，《数据库名，sql语句》》
     */


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
