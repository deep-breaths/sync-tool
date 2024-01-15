package com.example.script.utils.comparator.sqlfile;

import com.alibaba.druid.sql.ast.SQLIndexDefinition;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
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
        Map<String, Map<String, Map<String, Set<String>>>> allKeys = new HashMap<>();
        Map<String, List<String>> creates = getCreatesByDefault();
        creates.forEach((databaseName, tables) -> {
            try {
                List<String> diffStatements = compareTableSchema(tables, targetConn, databaseName);
                if (!diffStatements.isEmpty()) {
                    result.put(DIFF_TABLE, Map.of(databaseName, diffStatements));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Map<String, Map<String, Set<String>>> tableKeys = TableFileComparator.getPrimaryOrUniqueKeys(tables);
            allKeys.put(databaseName, tableKeys);

        });

        DiffDDL diffDDL = new DiffDDL();
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
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sourceTableDDL, JdbcConstants.MYSQL);
            List<SQLStatement> statements = parser.parseStatementList();
            String tableName = null;
            if (!statements.isEmpty()) {
                SQLStatement statement = statements.getFirst();
                if (statement instanceof MySqlCreateTableStatement createTableStatement) {
                    tableName = createTableStatement.getTableName();
                    tableName = tableName == null ? null : tableName.replace("`", "");
                    if (DBUtils.tableExistsInTarget(tableName, targetTables)) {
                        // 表存在于目标数据库，比较表结构
                        String targetTableDDL = DBUtils.showCreateTable(targetConn, databaseName, tableName);
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

    public static DiffDDL getDiffDDL(String targetCreatePath) {
        Map<String, Map<String, List<String>>> result = new HashMap<>();
        Map<String, Map<String, Map<String, Set<String>>>> allKeys = new HashMap<>();
        Map<String, List<String>> creates = getCreatesByDefault();
        creates.forEach((databaseName, tables) -> {
            List<String> diffStatements = compareTableSchema(tables, targetCreatePath, databaseName);
            if (!diffStatements.isEmpty()) {
                result.put(DIFF_TABLE, Map.of(databaseName, diffStatements));
            }
            Map<String, Map<String, Set<String>>> tableKeys = TableFileComparator.getPrimaryOrUniqueKeys(tables);
            allKeys.put(databaseName, tableKeys);

        });

        DiffDDL diffDDL = new DiffDDL();
        diffDDL.setDiffSchemas(result);
        diffDDL.setKeys(allKeys);
        return diffDDL;

    }
    public static List<String> compareTableSchema(List<String> sourceTables, String targetCreatePath,
                                                  String databaseName) {
        List<String> diffStatements = new ArrayList<>();


        Map<String, List<String>> targetALLCreates = getCreates(targetCreatePath);

        // 获取源数据库和目标数据库的表列表
        List<String> targetCreate = targetALLCreates.get(databaseName);

        Map<String, String> targetTables = formatCreateSql(targetCreate);

        for (String sourceTableDDL : sourceTables) {
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sourceTableDDL, JdbcConstants.MYSQL);
            List<SQLStatement> statements = parser.parseStatementList();
            String tableName = null;
            if (!statements.isEmpty()) {
                SQLStatement statement = statements.getFirst();
                if (statement instanceof MySqlCreateTableStatement createTableStatement) {
                    tableName = createTableStatement.getTableName();
                    tableName = tableName == null ? null : tableName.replace("`", "");
                    String targetTableDDL = targetTables.get(tableName);
                    if (targetTableDDL !=null&&!targetTableDDL.isEmpty()) {
                        // 表存在于目标数据库，比较表结构
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

    private static Map<String, String> formatCreateSql(List<String> tableDDLs) {
        Map<String, String> result = new HashMap<>();
        for (String tableDDL : tableDDLs) {
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(tableDDL, JdbcConstants.MYSQL);
            List<SQLStatement> statements = parser.parseStatementList();
            SQLStatement statement = statements.getFirst();
            if (statement instanceof MySqlCreateTableStatement createTableStatement) {
                String tableName = createTableStatement.getTableName();
                tableName = tableName == null ? null : tableName.replace("`", "");
                result.put(tableName,tableDDL);

            }
        }
        return result;
    }


        /**
         *
         * @return 《数据库名，建表语句》
         */
        public static Map<String, List<String>> getCreatesByDefault () {
            return FileUtils.getInitSQLByDefault().get(SQLSaveType.DDL_CREATE);
        }

        public static Map<String, List<String>> getCreates (String path){
            return FileUtils.getInit(path).get(SQLSaveType.DDL_CREATE);
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
        public static Map<String, Map<String,Set<String>>> getPrimaryOrUniqueKeys (List < String > tables) {
            Map<String, Map<String,Set<String>>> result = new HashMap<>();
            for (String tableDDL : tables) {
                TableKey tableKey = getPrimaryOrUniqueKeys(tableDDL);
                if (tableKey.getTableName() != null && !tableKey.getKeys().isEmpty()) {
                    result.put(tableKey.getTableName(), tableKey.getKeys());
                }
            }

            return result;
        }

        private static TableKey getPrimaryOrUniqueKeys (String tableDDL){
            Map<String,Set<String>> tableKeys = new HashMap<>();

            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(tableDDL, JdbcConstants.MYSQL);
            List<SQLStatement> statements = parser.parseStatementList();

            String tableName = null;
            if (!statements.isEmpty()) {
                SQLStatement statement = statements.getFirst();
                if (statement instanceof MySqlCreateTableStatement createTableStatement) {
                    tableName = createTableStatement.getTableName();
                    tableName = tableName == null ? null : tableName.replace("`", "");
                    MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
                    statement.accept(visitor);
                    List<MySqlKey> sqlKeys = createTableStatement.getMysqlKeys();
                    for (MySqlKey key : sqlKeys) {
                        Set<String> keys = new HashSet<>();
                        if (key instanceof MySqlPrimaryKey columns) {
                            SQLIndexDefinition indexDefinition = columns
                                    .getIndexDefinition();
                            indexDefinition
                                    .getColumns()
                                    .forEach(column -> keys.add(column.getExpr().toString().replace("`", "")));
                            tableKeys.put("primary",keys);
                        } else if (key instanceof MySqlUnique columns) {
                            SQLIndexDefinition indexDefinition = columns
                                    .getIndexDefinition();
                            indexDefinition
                                    .getColumns()
                                    .forEach(column -> keys.add(column.getExpr().toString().replace("`", "")));
                            SQLName name = indexDefinition.getName();
                            tableKeys.put(name==null?null:name.toString().replace("`", ""),keys);
                        }



                    }


                }
            }
            TableKey tableKey = new TableKey();
            tableKey.setTableName(tableName);
            tableKey.setKeys(tableKeys);
            return tableKey;
        }

    }
