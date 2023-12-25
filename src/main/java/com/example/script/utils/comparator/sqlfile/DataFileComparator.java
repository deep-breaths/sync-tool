package com.example.script.utils.comparator.sqlfile;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.example.script.domain.FetchData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author albert lewis
 * @date 2023/12/21
 */
public class DataFileComparator {

//    public static Map<String, Map<String, List<String>>> getDiffDML(Connection sourceConn, Connection targetConn) throws SQLException {
//        Map<String, Map<String, List<String>>> result = new HashMap<>();
//        List<String> databases = DBUtils.getAllDatabases(sourceConn);
//
//        for (String database : databases) {
//            List<String> tables = DBUtils.getTableNames(sourceConn, database);
//            List<String> inserts = new ArrayList<>();
//            List<String> updates = new ArrayList<>();
//            List<String> deletes = new ArrayList<>();
//            for (String table : tables) {
//                try {
//                    Set<String> keys = DBUtils.getPrimaryOrUniqueKeys(sourceConn, database, table);
//
//                    Map<Map<String, Object>, Map<String, Object>> sourceData = fetchData(sourceConn, database, table, keys);
//                    Map<Map<String, Object>, Map<String, Object>> targetData = fetchData(targetConn, database, table, keys);
//
//
//                    // 检测变化
//                    for (Map.Entry<Map<String, Object>, Map<String, Object>> entry : sourceData.entrySet()) {
//                        if (!targetData.containsKey(entry.getKey())) {
//                            inserts.add(BuildSQL.buildInsertSql(database, table, entry.getValue()));
//                        } else if (!entry.getValue().equals(targetData.get(entry.getKey()))) {
//                            updates.add(BuildSQL.buildUpdateSql(database, table, entry.getKey(), entry.getValue()));
//                        }
//                    }
//
//                    for (Map<String, Object> key : targetData.keySet()) {
//                        if (!sourceData.containsKey(key)) {
//                            deletes.add(BuildSQL.buildDeleteSql(database, table, key));
//                        }
//                    }
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//            if (inserts != null) {
//                result.put(DML_INSERT, Map.of(database, inserts));
//            }
//            if (updates != null) {
//                result.put(DML_UPDATE, Map.of(database, updates));
//            }
//            if (deletes != null) {
//                result.put(DML_DELETE, Map.of(database, deletes));
//            }
//        }
//        return result;
//
//    }


    /**
     * @param sqlList
     * @param keys
     * @return 表名，key字段名称和值，字段名称和值
     */
    public static Map<String, Map<Map<String, Object>, Map<String, Object>>> fetchData(List<String> sqlList,
                                                                                       Map<String, Set<String>> keys) {
        Map<String, Map<Map<String, Object>, Map<String, Object>>> data = new HashMap<>();
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
    SQLStatement sqlStatement = parser.parseStatement();
    if (sqlStatement instanceof MySqlInsertStatement insertStatement) {
        tableName = insertStatement.getTableName().getSimpleName();
        Set<String> tableKeys = keys.get(tableName);
        if (tableKeys!=null){
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
