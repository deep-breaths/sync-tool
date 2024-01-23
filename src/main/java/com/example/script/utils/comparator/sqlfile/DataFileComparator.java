package com.example.script.utils.comparator.sqlfile;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.example.script.constant.SQLSaveType;
import com.example.script.domain.FetchData;
import com.example.script.utils.comparator.BuildSQL;

import java.util.*;

import static com.example.script.constant.SQLSaveType.*;

/**
 * @author albert lewis
 * @date 2023/12/21
 */
public class DataFileComparator {

    public static Map<String, Map<String, List<String>>> getDiffDML(Map<String, Map<String, List<String>>> sourceInitSQL, Map<String, Map<String, List<String>>> targetInitSQL, Map<String,
            Map<String, Map<String, Set<String>>>> allTableKeys, Map<String, Map<String, Map<String, Set<String>>>> targetAllTableKeys) {
        Map<String, Map<String, List<String>>> result = new HashMap<>();
        if (targetInitSQL == null) {
            targetInitSQL = new LinkedHashMap<>();
        }
        boolean exist = !targetInitSQL.isEmpty();
        Map<String, Map<String, Set<String>>> allKeys = new LinkedHashMap<>();
        allTableKeys.forEach((databaseName, tables) -> {
            Map<String, Set<String>> tableKeys = new HashMap<>();
            Map<String, Map<String, Set<String>>> targetTableKeys = Optional.ofNullable(targetAllTableKeys.get(databaseName))
                                                                            .orElse(new LinkedHashMap<>());
            tables.forEach((tableName, sourceKeys) -> {
                Set<String> keys = null;
                if (exist) {
                    Map<String, Set<String>> targetKeys = Optional.ofNullable(targetTableKeys.get(tableName))
                                                                  .orElse(new LinkedHashMap<>());

                    for (Map.Entry<String, Set<String>> sourceKey : sourceKeys.entrySet()) {
                        Set<String> value = sourceKey.getValue();
                        if (targetKeys.containsValue(value)) {
                            keys = value;
                            break;
                        }
                    }
                }

                keys = keys == null ? sourceKeys
                        .entrySet()
                        .stream()
                        .findFirst()
                        .map(Map.Entry::getValue)
                        .orElse(null) : keys;
                tableKeys.put(tableName, keys);
            });
            allKeys.put(databaseName, tableKeys);


        });


        Map<String, List<String>> sourceInserts = sourceInitSQL.get(SQLSaveType.DML_INSERT);
        Map<String, List<String>> targetInserts = Optional.ofNullable(targetInitSQL.get(SQLSaveType.DML_INSERT))
                                                          .orElse(new LinkedHashMap<>());
        sourceInserts.forEach((databaseName, insetSQLs) -> {

            Map<String, Map<Map<String, Object>, Map<String, Object>>> sourceFetchData = DataFileComparator.fetchData(insetSQLs, allKeys.get(databaseName));
            Map<String, Map<Map<String, Object>, Map<String, Object>>> targetFetchData = DataFileComparator.fetchData(targetInserts.get(databaseName), allKeys.get(databaseName));
            List<String> inserts = new ArrayList<>();
            List<String> updates = new ArrayList<>();
            List<String> deletes = new ArrayList<>();
            sourceFetchData.forEach((tableName, sourceData) -> {
                Map<Map<String, Object>, Map<String, Object>> targetData =Optional.ofNullable(targetFetchData.get(tableName))
                                                                                  .orElse(new LinkedHashMap<>());
                // 检测变化
                for (Map.Entry<Map<String, Object>, Map<String, Object>> sourceEntry : sourceData.entrySet()) {
                    if (!targetData.containsKey(sourceEntry.getKey())) {
                        inserts.add(BuildSQL.buildInsertSql(databaseName, tableName, sourceEntry.getValue()));
                    } else if (!sourceEntry.getValue().equals(targetData.get(sourceEntry.getKey()))) {
                        updates.add(BuildSQL.buildUpdateSql(databaseName, tableName, sourceEntry.getKey(), sourceEntry.getValue()));
                    }
                }

                for (Map<String, Object> key : targetData.keySet()) {
                    if (!sourceData.containsKey(key)) {
                        deletes.add(BuildSQL.buildDeleteSql(databaseName, tableName, key));
                    }
                }

            });

            if (!inserts.isEmpty()) {
                result.computeIfAbsent(DML_INSERT, key -> new HashMap<>()).put(databaseName, inserts);
            }
            if (!updates.isEmpty()) {
                result.computeIfAbsent(DML_UPDATE, key -> new HashMap<>()).put(databaseName, updates);
            }
            if (!deletes.isEmpty()) {
                result.computeIfAbsent(DML_DELETE, key -> new HashMap<>()).put(databaseName, deletes);
            }
        });
        return result;

    }


    /**
     * @param sqlList
     * @param keys
     * @return 表名，key字段名称和值，字段名称和值
     */
    public static Map<String, Map<Map<String, Object>, Map<String, Object>>> fetchData(List<String> sqlList,
                                                                                       Map<String, Set<String>> keys) {
        Map<String, Map<Map<String, Object>, Map<String, Object>>> data = new HashMap<>();
        if (sqlList == null) {
            return data;
        }
        for (String sql : sqlList) {
            FetchData fetchData = fetchData(sql, keys);
            String tableName = fetchData.getTableName();
            Map<Map<String, Object>, Map<String, Object>> fetchDataData = fetchData.getData();
            if (tableName != null && !fetchDataData.isEmpty()) {
                if (!data.containsKey(tableName)) {
                    data.put(tableName, new HashMap<>());
                }
                data.get(tableName).putAll(fetchDataData);
            }

        }
        return data;
    }


    public static FetchData fetchData(String sql, Map<String, Set<String>> keys) {
        Map<Map<String, Object>, Map<String, Object>> data = new HashMap<>();

        String tableName = null;

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        SQLStatement sqlStatement = null;
        try {
            sqlStatement = parser.parseStatement();
        } catch (ParserException e) {
            System.out.println(sql);
            throw e;
        }

        if (sqlStatement instanceof MySqlInsertStatement insertStatement) {
            tableName = insertStatement.getTableName().getSimpleName();
            tableName = tableName == null ? null : tableName.replace("`", "");
            Set<String> tableKeys = keys.get(tableName);
            if (tableKeys != null) {
                FetchData fetchData = fetchData(sql, tableKeys);
                Map<Map<String, Object>, Map<String, Object>> keyValues = fetchData.getData();
                data.putAll(keyValues);
            }

        }
        FetchData fetchData = new FetchData();
        fetchData.setTableName(tableName);
        fetchData.setData(data);
        return fetchData;
    }

    public static FetchData fetchData(String sql, Set<String> keys) {
        Map<Map<String, Object>, Map<String, Object>> data = new HashMap<>();

        String tableName = null;

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        SQLStatement sqlStatement = parser.parseStatement();
        if (sqlStatement instanceof MySqlInsertStatement insertStatement) {
            tableName = insertStatement.getTableName().getSimpleName();
            tableName = tableName == null ? null : tableName.replace("`", "");
            //StringBuilder out = new StringBuilder();
//                MySqlExportParameterVisitor visitor=new MySqlExportParameterVisitor(out);
            MySqlExportParameterVisitor visitor = new MySqlExportParameterVisitor();
            insertStatement.accept(visitor);
            List<SQLExpr> columns = insertStatement.getColumns();
            List<Object> columnValues = visitor.getParameters();//List<SQLExpr> columnValues =insertStatement.getValues().getValues()
            //字段名称和值
            Map<String, Object> row = new HashMap<>();
            //key字段名称和值
            Map<String, Object> keyValues = new HashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                SQLExpr columnExpr = columns.get(i);
                Object value = columnValues.get(i);
                String column = columnExpr.toString();
                column = column == null ? null : column.replace("`", "");
                row.put(column, value);
                if (keys.contains(column)) {
                    keyValues.put(column, value);
                }
            }
            data.put(keyValues, row);
        }
        FetchData fetchData = new FetchData();
        fetchData.setTableName(tableName);
        fetchData.setData(data);
        return fetchData;
    }

}
