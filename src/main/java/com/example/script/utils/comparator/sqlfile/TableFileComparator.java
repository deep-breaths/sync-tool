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
import com.example.script.domain.DiffDDL;
import com.example.script.domain.TableKey;
import com.example.script.utils.comparator.BuildSQL;

import java.util.*;

import static com.example.script.constant.SQLSaveType.DIFF_TABLE;

/**
 * @author albert lewis
 * @date 2023/12/20
 */
public class TableFileComparator {

    public static DiffDDL getDiffDDL(Map<String, List<String>> sourceCreates, Map<String, List<String>> targetCreates) {
        Map<String, Map<String, List<String>>> result = new HashMap<>();
        Map<String, Map<String, Map<String, Set<String>>>> allKeys = new HashMap<>();
        sourceCreates.forEach((databaseName, tables) -> {
            List<String> diffStatements = compareTableSchema(tables, targetCreates, databaseName);
            if (!diffStatements.isEmpty()) {
                result.computeIfAbsent(DIFF_TABLE, key -> new HashMap<>()).put(databaseName, diffStatements);
            }
            Map<String, Map<String, Set<String>>> tableKeys = TableFileComparator.getPrimaryOrUniqueKeys(tables);
            allKeys.put(databaseName, tableKeys);

        });

        DiffDDL diffDDL = new DiffDDL();
        diffDDL.setDiffSchemas(result);
        diffDDL.setSourceKeys(allKeys);
        return diffDDL;

    }

    public static List<String> compareTableSchema(List<String> sourceTables, Map<String, List<String>> targetALLCreates,
                                                  String databaseName) {
        List<String> diffStatements = new ArrayList<>();

        // 获取源数据库和目标数据库的表列表
        List<String> targetCreate = Optional.ofNullable(targetALLCreates.get(databaseName))
                                            .orElse(new ArrayList<>());


        if (targetCreate==null||targetCreate.isEmpty()) {
            String createStatement = String.format("CREATE DATABASE IF NOT EXISTS `%s` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;", databaseName);
            diffStatements.add(createStatement);
        }
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
                    if (targetTableDDL != null && !targetTableDDL.isEmpty()) {
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
        if (tableDDLs==null){
            return result;
        }
        for (String tableDDL : tableDDLs) {
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(tableDDL, JdbcConstants.MYSQL);
            List<SQLStatement> statements = parser.parseStatementList();
            SQLStatement statement = statements.getFirst();
            if (statement instanceof MySqlCreateTableStatement createTableStatement) {
                String tableName = createTableStatement.getTableName();
                tableName = tableName == null ? null : tableName.replace("`", "");
                result.put(tableName, tableDDL);

            }
        }
        return result;
    }

    /**
     * 根据建表语句查询主键或唯一键
     *
     * @param targetCreates
     * @return
     */
    public static Map<String, Map<String, Map<String, Set<String>>>> getAllPrimaryOrUniqueKeys(Map<String, List<String>> targetCreates) {
        Map<String, Map<String, Map<String, Set<String>>>> allKeys = new HashMap<>();
        if (targetCreates == null) {
            return allKeys;
        }
        targetCreates.forEach((databaseName, tables) -> {
            Map<String, Map<String, Set<String>>> tableKeys = getPrimaryOrUniqueKeys(tables);
            allKeys.put(databaseName, tableKeys);

        });

        return allKeys;
    }

    public static Map<String, Map<String, Set<String>>> getPrimaryOrUniqueKeys(List<String> tables) {
        Map<String, Map<String, Set<String>>> result = new HashMap<>();
        for (String tableDDL : tables) {
            TableKey tableKey = getPrimaryOrUniqueKeys(tableDDL);
            if (tableKey.getTableName() != null && !tableKey.getKeys().isEmpty()) {
                result.put(tableKey.getTableName(), tableKey.getKeys());
            }
        }

        return result;
    }

    private static TableKey getPrimaryOrUniqueKeys(String tableDDL) {
        Map<String, Set<String>> tableKeys = new HashMap<>();

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
                                .forEach(column -> {
                                    String columnName = column.getExpr().toString().replace("`", "");
                                    tableKeys.put("primary_" + columnName, Set.of(columnName));
                                });
                        tableKeys.put("primary", keys);
                    } else if (key instanceof MySqlUnique columns) {
                        SQLIndexDefinition indexDefinition = columns
                                .getIndexDefinition();
                        indexDefinition
                                .getColumns()
                                .forEach(column -> keys.add(column.getExpr().toString().replace("`", "")));
                        SQLName name = indexDefinition.getName();
                        tableKeys.put(name == null ? null : name.toString().replace("`", ""), keys);
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
