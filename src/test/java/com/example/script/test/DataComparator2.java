package com.example.script.test;

/**
 * @author albert lewis
 * @date 2023/12/21
 */
public class DataComparator2 {
    public static void main(String[] args) {
//        // ...
//
//        // 获取数据库1的表数据
//        List<String> tables1 = getTableNames(conn1);
//        for (String table : tables1) {
//            List<String> columns1 = getTableColumns(conn1, table);
//            List<List<Object>> data1 = getTableData(conn1, table, columns1);
//
//            // 获取数据库2的表数据
//            List<String> columns2 = getTableColumns(conn2, table);
//            List<List<Object>> data2 = getTableData(conn2, table, columns2);
//
//            // 对比表数据差异并生成差异化语句
//            for (List<Object> row1 : data1) {
//                boolean found = false;
//                for (List<Object> row2 : data2) {
//                    if (isSameData(row1, row2, columns1, columns2)) {
//                        found = true;
//                        // 检查是否需要更新
//                        if (!isDataEqual(row1, row2, columns1, columns2)) {
//                            // row1和row2是相同的数据，但内容不同，生成更新语句
//                            String updateSql = generateUpdateSql(table, columns1, row1, row2);
//                            System.out.println(updateSql);
//                        }
//                        break;
//                    }
//                }
//                if (!found) {
//                    // row1是数据库1中的数据，在数据库2中不存在，生成插入语句
//                    String insertSql = generateInsertSql(table, columns1, row1);
//                    System.out.println(insertSql);
//                }
//            }
//
//            for (List<Object> row2 : data2) {
//                boolean found = false;
//                for (List<Object> row1 : data1) {
//                    if (isSameData(row2, row1, columns2, columns1)) {
//                        found = true;
//                        break;
//                    }
//                }
//                if (!found) {
//                    // row2是数据库2中的数据，在数据库1中不存在，生成删除语句
//                    String deleteSql = generateDeleteSql(table, columns2, row2);
//                    System.out.println(deleteSql);
//                }
//            }
//        }
//
//        // 关闭数据库连接
//        try {
//            conn1.close();
//            conn2.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    // ...

    // 判断两行数据是否为同一条数据
//    private static boolean isSameData(List<Object> row1, List<Object> row2, List<String> columns1, List<String> columns2) {
//        // 根据主键或唯一索引判断是否为同一条数据
//        for (String column : columns1) {
//            if (isPrimaryKeyOrUnique(conn1, column, columns1.get(0))) {
//                int index1 = columns1.indexOf(column);
//                int index2 = columns2.indexOf(column);
//                Object value1 = row1.get(index1);
//                Object value2 = row2.get(index2);
//                if (value1 == null && value2 == null) {
//                    continue;
//                }
//                if (value1 != null && value2 != null && value1.equals(value2)) {
//                    continue;
//                }
//                return false;
//            }
//        }
//        return true;
//    }
//
//    // 判断列是否为主键或唯一索引
//    private static boolean isPrimaryKeyOrUnique(Connection conn, String tableName, String columnName) {
//        // 查询表的元数据信息
//        DatabaseMetaData metaData;
//        try {
//            metaData = conn.getMetaData();
//            ResultSet rs = metaData.getIndexInfo(null, null, tableName, true, false);
//            while (rs.next()) {
//                String indexName = rs.getString("INDEX_NAME");
//                boolean nonUnique = rs.getBoolean("NON_UNIQUE");
//                if (!nonUnique && columnName.equals(rs.getString("COLUMN_NAME"))) {
//                    return true;
//                }
//            }
//            rs.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // 生成插入语句
//    private static String generateInsertSql(String table, List<String> columns, List<Object> data) {
//        StringBuilder sql = new StringBuilder();
//        sql.append("INSERT INTO ").append(table).append(" (");
//        for (String column : columns) {
//            sql.append(column).append(", ");
//        }
//        sql.delete(sql.length() - 2, sql.length());
//        sql.append(") VALUES (");
//        for (Object value : data) {
//            if (value == null) {
//                sql.append("NULL, ");
//            } else {
//                sql.append("'").append(value.toString()).append("', ");
//            }
//        }
//        sql.delete(sql.length() - 2, sql.length());
//        sql.append(")");
//        return sql.toString();
//    }
//
//    // 判断两行数据的内容是否相同
//    private static boolean isDataEqual(List<Object> row1, List<Object> row2, List<String> columns1, List<String> columns2) {
//        for (String column : columns1) {
//            int index1 = columns1.indexOf(column);
//            int index2 = columns2.indexOf(column);
//            Object value1 = row1.get(index1);
//            Object value2 = row2.get(index2);
//            if (value1 == null && value2 == null) {
//                continue;
//            }
//            if (value1 != null && value2 != null && value1.equals(value2)) {
//                continue;
//            }
//            return false;
//        }
//        return true;
//    }
//
//    // 生成更新语句
//    private static String generateUpdateSql(String table, List<String> columns, List<Object> data1, List<Object> data2) {
//        StringBuilder sql = new StringBuilder();
//        sql.append("UPDATE ").append(table).append(" SET ");
//        for (String column : columns) {
//            int index = columns.indexOf(column);
//            Object value1 = data1.get(index);
//            Object value2 = data2.get(index);
//            if (!value1.equals(value2)) {
//                sql.append(column).append(" = '").append(value1.toString()).append("', ");
//            }
//        }
//        sql.delete(sql.length() - 2, sql.length());
//
//        // 添加 WHERE 条件，根据主键或唯一索引判断更新的数据
//        sql.append(" WHERE ");
//        for (String column : columns) {
//            if (isPrimaryKeyOrUnique(conn1, column, columns1.get(0))) {
//                int index1 = columns1.indexOf(column);
//                int index2 = columns2.indexOf(column);
//                Object value1 = data1.get(index1);
//                Object value2 = data2.get(index2);
//                if (value1 == null) {
//                    sql.append(column).append(" IS NULL AND ");
//                } else {
//                    sql.append(column).append(" = '").append(value1.toString()).append("' AND ");
//                }
//            }
//        }
//        sql.delete(sql.length() - 5, sql.length());
//        return sql.toString();
//    }
//
//
//    // 生成删除语句
//    private static String generateDeleteSql(String table, List<String> columns, List<Object> data) {
//        StringBuilder sql = new StringBuilder();
//        sql.append("DELETE FROM ").append(table).append(" WHERE ");
//        for (String column : columns) {
//            int index = columns.indexOf(column);
//            Object value = data.get(index);
//            if (value == null) {
//                sql.append(column).append(" IS NULL AND ");
//            } else {
//                sql.append(column).append(" = '").append(value.toString()).append("' AND ");
//            }
//        }
//        sql.delete(sql.length() - 5, sql.length());
//        return sql.toString();
//    }
//
//
//    public static void main(String[] args) {
//        try (Connection sourceConnection = DriverManager.getConnection(sourceUrl, sourceUser, sourcePassword);
//             Connection targetConnection = DriverManager.getConnection(targetUrl, targetUser, targetPassword)) {
//
//            Map<Object, Map<String, Object>> sourceData = fetchData(sourceConnection, tableName, primaryKey);
//            Map<Object, Map<String, Object>> targetData = fetchData(targetConnection, tableName, primaryKey);
//
//            List<String> insertStatements = new ArrayList<>();
//            List<String> updateStatements = new ArrayList<>();
//            List<String> deleteStatements = new ArrayList<>();
//
//            // 检查插入和更新
//            for (Map.Entry<Object, Map<String, Object>> entry : sourceData.entrySet()) {
//                if (!targetData.containsKey(entry.getKey())) {
//                    insertStatements.add(createInsertStatement(tableName, entry.getValue()));
//                } else if (!entry.getValue().equals(targetData.get(entry.getKey()))) {
//                    updateStatements.add(createUpdateStatement(tableName, primaryKey, entry.getValue()));
//                }
//            }
//
//            // 检查删除
//            for (Object key : targetData.keySet()) {
//                if (!sourceData.containsKey(key)) {
//                    deleteStatements.add(createDeleteStatement(tableName, primaryKey, key));
//                }
//            }
//
//            // 输出或执行 SQL 语句
//            System.out.println("Insert Statements: " + insertStatements);
//            System.out.println("Update Statements: " + updateStatements);
//            System.out.println("Delete Statements: " + deleteStatements);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static Map<Object, Map<String, Object>> fetchData(Connection connection, String tableName, String primaryKey) throws SQLException {
//        Map<Object, Map<String, Object>> data = new HashMap<>();
//        String query = "SELECT * FROM " + tableName;
//        try (Statement stmt = connection.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            ResultSetMetaData metaData = rs.getMetaData();
//            int columnCount = metaData.getColumnCount();
//
//            while (rs.next()) {
//                Map<String, Object> row = new HashMap<>();
//                Object key = rs.getObject(primaryKey);
//                for (int i = 1; i <= columnCount; i++) {
//                    row.put(metaData.getColumnName(i), rs.getObject(i));
//                }
//                data.put(key, row);
//            }
//        }
//        return data;
//    }
//    public static void main(String[] args) {
//        try {
//            Connection sourceConn = DriverManager.getConnection(SOURCE_DB_URL, USER, PASS);
//            Connection targetConn = DriverManager.getConnection(TARGET_DB_URL, USER, PASS);
//
//            Map<Object, Map<String, Object>> sourceData = fetchData(sourceConn, TABLE_NAME);
//            Map<Object, Map<String, Object>> targetData = fetchData(targetConn, TABLE_NAME);
//
//            List<String> inserts = new ArrayList<>();
//            List<String> updates = new ArrayList<>();
//            List<String> deletes = new ArrayList<>();
//
//            // Detect changes
//            for (Map.Entry<Object, Map<String, Object>> entry : sourceData.entrySet()) {
//                Object key = entry.getKey();
//                if (!targetData.containsKey(key)) {
//                    inserts.add(buildInsertSql(TABLE_NAME, entry.getValue()));
//                } else if (!entry.getValue().equals(targetData.get(key))) {
//                    updates.add(buildUpdateSql(TABLE_NAME, PRIMARY_KEY, key, entry.getValue()));
//                }
//            }
//
//            for (Object key : targetData.keySet()) {
//                if (!sourceData.containsKey(key)) {
//                    deletes.add(buildDeleteSql(TABLE_NAME, PRIMARY_KEY, key));
//                }
//            }
//
//            // Display or execute SQL statements
//            System.out.println("Insert Statements: " + inserts);
//            System.out.println("Update Statements: " + updates);
//            System.out.println("Delete Statements: " + deletes);
//
//            sourceConn.close();
//            targetConn.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static Map<Object, Map<String, Object>> fetchData(Connection conn, String tableName) throws SQLException {
//        Map<Object, Map<String, Object>> data = new HashMap<>();
//        Statement stmt = conn.createStatement();
//        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
//        ResultSetMetaData metaData = rs.getMetaData();
//
//        while (rs.next()) {
//            int numColumns = metaData.getColumnCount();
//            Map<String, Object> row = new HashMap<>();
//            Object keyVal = null;
//            for (int i = 1; i <= numColumns; i++) {
//                String column = metaData.getColumnName(i);
//                Object value = rs.getObject(column);
//                row.put(column, value);
//                if (column.equalsIgnoreCase(PRIMARY_KEY)) {
//                    keyVal = value;
//                }
//            }
//            data.put(keyVal, row);
//        }
//
//        rs.close();
//        stmt.close();
//        return data;
//    }
//
//    private static String buildInsertSql(String tableName, Map<String, Object> data) {
//        String columns = String.join(", ", data.keySet());
//        String values = String.join(", ", Collections.nCopies(data.size(), "?"));
//        return "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
//        // 实际应用中应绑定数据到占位符
//    }
//
//    private static String buildUpdateSql(String tableName, String primaryKey, Object keyValue, Map<String, Object> data) {
//        String setClause = String.join(", ", data.keySet().stream()
//                                                 .map(key -> key + " = ?").collect(Collectors.toList()));
//        return "UPDATE " + tableName + " SET " + setClause + " WHERE " + primaryKey + " = " + keyValue;
//        // 实际应用中应绑定数据到占位
//    }
//
//
////todo
//    public static void main(String[] args) {
//        try {
//            Connection sourceConn = DriverManager.getConnection(SOURCE_DB_URL, USER, PASS);
//            Connection targetConn = DriverManager.getConnection(TARGET_DB_URL, USER, PASS);
//
//            // 获取主键或唯一索引
//            Set<String> keys = getPrimaryOrUniqueKeys(sourceConn, TABLE_NAME);
//
//            Map<Map<String, Object>, Map<String, Object>> sourceData = fetchData(sourceConn, TABLE_NAME, keys);
//            Map<Map<String, Object>, Map<String, Object>> targetData = fetchData(targetConn, TABLE_NAME, keys);
//
//            List<String> inserts = new ArrayList<>();
//            List<String> updates = new ArrayList<>();
//            List<String> deletes = new ArrayList<>();
//
//            // 检测变化
//            for (Map.Entry<Map<String, Object>, Map<String, Object>> entry : sourceData.entrySet()) {
//                if (!targetData.containsKey(entry.getKey())) {
//                    inserts.add(buildInsertSql(TABLE_NAME, entry.getValue()));
//                } else if (!entry.getValue().equals(targetData.get(entry.getKey()))) {
//                    updates.add(buildUpdateSql(TABLE_NAME, entry.getKey(), entry.getValue()));
//                }
//            }
//
//            for (Map<String, Object> key : targetData.keySet()) {
//                if (!sourceData.containsKey(key)) {
//                    deletes.add(buildDeleteSql(TABLE_NAME, key));
//                }
//            }
//
//            // 展示或执行 SQL 语句
//            System.out.println("Insert Statements: " + inserts);
//            System.out.println("Update Statements: " + updates);
//            System.out.println("Delete Statements: " + deletes);
//
//            sourceConn.close();
//            targetConn.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static Set<String> getPrimaryOrUniqueKeys(Connection conn, String tableName) throws SQLException {
//        Set<String> keys = new HashSet<>();
//        DatabaseMetaData metaData = conn.getMetaData();
//        ResultSet rs = metaData.getPrimaryKeys(null, null, tableName);
//        while (rs.next()) {
//            keys.add(rs.getString("COLUMN_NAME"));
//        }
//        rs.close();
//
//        // 如果没有主键，尝试获取唯一索引
//        if (keys.isEmpty()) {
//            rs = metaData.getIndexInfo(null, null, tableName, true, true);
//            while (rs.next()) {
//                keys.add(rs.getString("COLUMN_NAME"));
//            }
//            rs.close();
//        }
//
//        return keys;
//    }
//
//    private static Map<Map<String, Object>, Map<String, Object>> fetchData(Connection conn, String tableName, Set<String> keys) throws SQLException {
//        Map<Map<String, Object>, Map<String, Object>> data = new HashMap<>();
//        Statement stmt = conn.createStatement();
//        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
//        ResultSetMetaData metaData = rs.getMetaData();
//
//        while (rs.next()) {
//            int numColumns = metaData.getColumnCount();
//            Map<String, Object> row = new HashMap<>();
//            Map<String, Object> keyValues = new HashMap<>();
//            for (int i = 1; i <= numColumns; i++) {
//                String column = metaData.getColumnName(i);
//                Object value = rs.getObject(column);
//                row.put(column, value);
//                if (keys.contains(column)) {
//                    keyValues.put(column, value);
//                }
//            }
//            data.put(keyValues, row);
//        }
//
//        rs.close();
//        stmt.close();
//        return data;
//    }
//
//    private static String buildInsertSql(String tableName, Map<String, Object> rowData) {
//        String columns = String.join(", ", rowData.keySet());
//        String values = rowData.values().stream()
//                               .map(value -> value instanceof String ? "'" + value + "'" : value.toString())
//                               .collect(Collectors.joining(", "));
//
//        return "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
//    }
//
//    private static String buildUpdateSql(String tableName, Map<String, Object> keyValues, Map<String, Object> rowData) {
//        String setClause = rowData.entrySet().stream()
//                                  .map(entry -> entry.getKey() + " = " + (entry.getValue() instanceof String ? "'" + entry.getValue() + "'" : entry.getValue()))
//                                  .collect(Collectors.joining(", "));
//
//        String whereClause = keyValues.entrySet().stream()
//                                      .map(entry -> entry.getKey() + " = " + (entry.getValue() instanceof String ? "'" + entry.getValue() + "'" : entry.getValue()))
//                                      .collect(Collectors.joining(" AND "));
//
//        return "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause;
//    }
//    private static String buildDeleteSql(String tableName, Map<String, Object> keyValues) {
//        String whereClause = keyValues.entrySet().stream()
//                                      .map(entry -> entry.getKey() + " = " + (entry.getValue() instanceof String ? "'" + entry.getValue() + "'" : entry.getValue()))
//                                      .collect(Collectors.joining(" AND "));
//
//        return "DELETE FROM " + tableName + " WHERE " + whereClause;
//    }



}
