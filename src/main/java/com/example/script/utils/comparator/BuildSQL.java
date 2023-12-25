package com.example.script.utils.comparator;

import com.alibaba.druid.sql.ast.SQLIndexDefinition;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.example.script.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author albert lewis
 * @date 2023/12/22
 */
public class BuildSQL {
    public static String buildInsertSql(String databaseName, String tableName, Map<String, Object> rowData) {
        String columns = String.join(", ", rowData.keySet());
        String values = rowData.values().stream()
                               .map(DBUtils::convertJavaType)
                               .collect(Collectors.joining(", "));

        String resultSQL;
        if (databaseName != null) {
            String format = "INSERT INTO `%s`.`%s` (%s) VALUES (%s);";
            resultSQL = String.format(format, databaseName, tableName, columns, values);
        } else {
            String format = "INSERT INTO `%s` (%s) VALUES (%s);";
            resultSQL = String.format(format, tableName, columns, values);
        }

        return resultSQL;
    }

    public static String buildUpdateSql(String databaseName, String tableName, Map<String, Object> keyValues, Map<String,
            Object> rowData) {
        String setClause = rowData.entrySet().stream()
                                  .map(entry -> STR."\{entry.getKey()} = \{DBUtils.convertJavaType(entry.getValue())}")
                                  .collect(Collectors.joining(", "));

        String whereClause = keyValues.entrySet().stream()
                                      .map(entry -> STR."\{entry.getKey()} = \{DBUtils.convertJavaType(entry.getValue())}")
                                      .collect(Collectors.joining(" AND "));

        String resultSQL;
        if (databaseName != null) {
            String format = "UPDATE `%s`.`%s` SET %s WHERE %s;";
            resultSQL = String.format(format, databaseName, tableName, setClause, whereClause);
        } else {
            String format = "UPDATE `%s` SET %s WHERE %s;";
            resultSQL = String.format(format, tableName, setClause, whereClause);
        }

        return resultSQL;
    }

    public static String buildDeleteSql(String databaseName, String tableName, Map<String, Object> keyValues) {
        String whereClause = keyValues.entrySet().stream()
                                      .map(entry -> STR."\{entry.getKey()} = \{DBUtils.convertJavaType(entry.getValue())}")
                                      .collect(Collectors.joining(" AND "));
        String resultSQL;
        if (databaseName != null) {
            String format = "DELETE FROM `%s`.`%s` WHERE %s;";
            resultSQL = String.format(format, databaseName, tableName, whereClause);
        } else {
            String format = "DELETE FROM `%s` WHERE %s;";
            resultSQL = String.format(format, tableName, whereClause);
        }

        return resultSQL;
    }
    public static List<String> generateAlterTableSQL(String sourceTableDDL, String targetTableDDL) {
        List<String> alterTableSQLs=new ArrayList<>();
        // 解析源表的DDL语句
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sourceTableDDL, JdbcConstants.MYSQL);
        SQLStatement sourceStatement = parser.parseStatement();
        // 解析目标表的DDL语句
        parser = SQLParserUtils.createSQLStatementParser(targetTableDDL, JdbcConstants.MYSQL);
        SQLStatement targetStatement = parser.parseStatement();

        if (sourceStatement instanceof MySqlCreateTableStatement sourceCreateTable && targetStatement instanceof MySqlCreateTableStatement targetCreateTable) {

            // 获取源表和目标表的表名
            String sourceTableName = sourceCreateTable.getTableName();
            String targetTableName = sourceCreateTable.getTableName();


            // 生成ALTER TABLE语句
            List<SQLColumnDefinition> sourceTableElements = sourceCreateTable.getColumnDefinitions();
            List<SQLColumnDefinition> targetTableElements = targetCreateTable.getColumnDefinitions();

            List<String> addedColumns = new ArrayList<>();
            List<String> modifiedColumns = new ArrayList<>();
            List<String> removedColumns = new ArrayList<>();

            String lastTargetColumn = null;

            for (SQLColumnDefinition targetColumn : targetTableElements) {
                boolean found = false;
                String lastSourceColumn = null;
                for (SQLColumnDefinition sourceColumn : sourceTableElements) {
                    if (sourceColumn.getName().toString().equalsIgnoreCase(targetColumn.getName().toString())) {
                        found = true;
                        if (!sourceColumn.toString().equalsIgnoreCase(targetColumn.toString())) {
                            if (lastSourceColumn == null) {
                                modifiedColumns.add(String.format("ALTER TABLE %s MODIFY COLUMN %s ;", targetTableName,
                                                                  sourceColumn));
                            } else {
                                modifiedColumns.add(String.format("ALTER TABLE %s MODIFY COLUMN %s AFTER %s;",
                                                                  targetTableName, sourceColumn, lastSourceColumn));
                            }
                        } else if (lastSourceColumn != null && !lastSourceColumn.equals(lastTargetColumn)) {
                            modifiedColumns.add(String.format("ALTER TABLE %s MODIFY COLUMN %s AFTER %s;",
                                                              targetTableName, sourceColumn, lastSourceColumn));
                        }
                        break;
                    }
                    lastSourceColumn = sourceColumn.getColumnName();
                }
                if (!found) {
                    removedColumns.add(String.format("ALTER TABLE %s DROP COLUMN %s;", targetTableName,
                                                     targetColumn.getColumnName()));
                }
                lastTargetColumn = targetColumn.getColumnName();
            }
            String lastSourceColumn = null;
            for (SQLColumnDefinition sourceColumn : sourceTableElements) {

                boolean found = false;
                for (SQLColumnDefinition targetColumn : targetTableElements) {
                    if (targetColumn.getName().toString().equalsIgnoreCase(sourceColumn.getName().toString())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {

                    if (lastSourceColumn != null) {
                        addedColumns.add(String.format("ALTER TABLE %s ADD COLUMN %s AFTER %s;",
                                                       targetTableName, sourceColumn, lastSourceColumn));
                    } else {
                        addedColumns.add(String.format("ALTER TABLE %s ADD COLUMN %s;", targetTableName, sourceColumn));
                    }

                }
                lastSourceColumn = sourceColumn.getColumnName();
            }

            List<MySqlKey> sourceKeys = sourceCreateTable.getMysqlKeys();
            List<MySqlKey> targetKeys = targetCreateTable.getMysqlKeys();
            List<String> addedKeys = new ArrayList<>();
            List<String> removedKeys = new ArrayList<>();
            for (MySqlKey targetKey : targetKeys) {
                boolean found = false;
                for (MySqlKey sourceKey : sourceKeys) {
                    if (sourceKey.toString().equalsIgnoreCase(targetKey.toString())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    SQLIndexDefinition indexDefinition = targetKey.getIndexDefinition();
                    if (!"PRIMARY".equals(indexDefinition.getType())) {
                        removedKeys.add(String.format("ALTER TABLE %s DROP INDEX %s;", targetTableName, targetKey.getName()));
                    }

                }
            }
            for (MySqlKey sourceKey : sourceKeys) {

                boolean found = false;
                for (MySqlKey targetKey : targetKeys) {
                    if (targetKey.toString().equalsIgnoreCase(sourceKey.toString())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    SQLIndexDefinition indexDefinition = sourceKey.getIndexDefinition();
                    if ("PRIMARY".equals(indexDefinition.getType())) {
                        removedKeys.add(String.format("ALTER TABLE %s DROP PRIMARY KEY,ADD %s;", targetTableName, sourceKey));
                    } else {
                        addedKeys.add(String.format("ALTER TABLE %s ADD %s;", targetTableName, sourceKey));
                    }

                }
            }
            alterTableSQLs.addAll(addedColumns);
            alterTableSQLs.addAll(modifiedColumns);
            alterTableSQLs.addAll(removedColumns);
            alterTableSQLs.addAll(addedKeys);
            alterTableSQLs.addAll(removedKeys);

        }


        return alterTableSQLs;
    }
}
