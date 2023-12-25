package test;

/**
 * @author albert lewis
 * @date 2023/12/20
 */

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.script.constant.DBConstant.*;

public class Migration2Tool {

    public static void main(String[] args) throws SQLException {
        DruidDataSource dataSource = createDataSource(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);

        DruidPooledConnection conn = dataSource.getConnection();
        List<String> createTableStatements = generateCreateTableStatements2(conn);
        List<String> insertDataStatements = generateInsertDataStatements(dataSource);
        System.out.println("*******建表语句************");
        createTableStatements.forEach(x-> System.out.println(x+"\t"));
        System.out.println("**********插入数据************");
        insertDataStatements.forEach(System.out::println);

        dataSource.close();
    }

    private static DruidDataSource createDataSource(String url, String username, String password) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    public static List<String> getTableNames(Connection conn, String databaseName) throws SQLException {
        List<String> tables = new ArrayList<>();
        ResultSet rs = conn.getMetaData().getTables(databaseName, null, "%", null);

        while (rs.next()) {
            tables.add(rs.getString(3)); // 获取表名
        }

        rs.close();
        return tables;
    }

    private static List<String> generateCreateTableStatements(DruidDataSource dataSource) throws SQLException {
        List<String> statements = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, null, new String[]{"TABLE"});

        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, null);
            ResultSet primaryKeyResultSet = metaData.getPrimaryKeys(null, null, tableName);
            ResultSet foreignKeyResultSet = metaData.getImportedKeys(null, null, tableName);
            ResultSet indexResultSet = metaData.getIndexInfo(null, null, tableName, false, false);

            StringBuilder statementBuilder = new StringBuilder();
            statementBuilder.append("CREATE TABLE ").append(tableName).append(" (");

            while (columnsResultSet.next()) {
                String columnName = columnsResultSet.getString("COLUMN_NAME");
                String columnType = columnsResultSet.getString("TYPE_NAME");
                int columnSize = columnsResultSet.getInt("COLUMN_SIZE");
                String remarks = columnsResultSet.getString("REMARKS");

                statementBuilder.append(columnName).append(" ").append(columnType);
                if (columnSize > 0) {
                    statementBuilder.append("(").append(columnSize).append(")");
                }
                statementBuilder.append(",");

                // Add column comments as comments in the create table statement
                if (remarks != null && !remarks.isEmpty()) {
                    statementBuilder.append(" COMMENT '").append(remarks).append("',");
                }
            }

            // Add primary key constraint
            while (primaryKeyResultSet.next()) {
                String columnName = primaryKeyResultSet.getString("COLUMN_NAME");
                statementBuilder.append("PRIMARY KEY (").append(columnName).append("),");
            }

            // Add foreign key constraints
            while (foreignKeyResultSet.next()) {
                String columnName = foreignKeyResultSet.getString("FKCOLUMN_NAME");
                String referencedTableName = foreignKeyResultSet.getString("PKTABLE_NAME");
                String referencedColumnName = foreignKeyResultSet.getString("PKCOLUMN_NAME");
                statementBuilder.append("FOREIGN KEY (").append(columnName).append(") REFERENCES ")
                                .append(referencedTableName).append("(").append(referencedColumnName).append("),");
            }

            // Add index definitions
            while (indexResultSet.next()) {
                String indexName = indexResultSet.getString("INDEX_NAME");
                String columnName = indexResultSet.getString("COLUMN_NAME");
                boolean nonUnique = indexResultSet.getBoolean("NON_UNIQUE");

                // Append index definition
                statementBuilder.append(nonUnique ? "INDEX" : "UNIQUE INDEX")
                                .append(" ")
                                .append(indexName)
                                .append(" (")
                                .append(columnName)
                                .append("), ");
            }
            if (indexResultSet.getRow() > 0) {
                statementBuilder.setLength(statementBuilder.length() - 2);
            }
            statements.add(statementBuilder.toString());

        }
        resultSet.close();
        connection.close();

        return statements;

    }
    private static List<String> generateCreateTableStatements2(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        String catalog = conn.getCatalog();
        ResultSet resultSet = metaData.getTables(catalog, null, null, new String[]{"TABLE"});
        List<String> createTableStatements = new ArrayList<>();
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            ResultSet columnsResultSet = metaData.getColumns(catalog, null, tableName, null);
            ResultSet primaryKeyResultSet = metaData.getPrimaryKeys(catalog, null, tableName);
            ResultSet foreignKeyResultSet = metaData.getImportedKeys(catalog, null, tableName);
            ResultSet indexResultSet = metaData.getIndexInfo(catalog, null, tableName, false, false);

            StringBuilder statementBuilder = new StringBuilder();
            statementBuilder.append("CREATE TABLE ").append(tableName).append(" (");

            List<String> columnDefinitions = new ArrayList<>();
            Map<String, String> indexDefinitions = new HashMap<>();

            while (columnsResultSet.next()) {
                String columnName = columnsResultSet.getString("COLUMN_NAME");
                String columnType = columnsResultSet.getString("TYPE_NAME");
                int columnSize = columnsResultSet.getInt("COLUMN_SIZE");
                String remarks = columnsResultSet.getString("REMARKS");

                StringBuilder columnDefinitionBuilder = new StringBuilder();
                columnDefinitionBuilder.append(columnName).append(" ").append(columnType);
                if (columnSize > 0) {
                    columnDefinitionBuilder.append("(").append(columnSize).append(")");
                }

                if (remarks != null && !remarks.isEmpty()) {
                    columnDefinitionBuilder.append(" COMMENT '").append(remarks).append("'");
                }

                columnDefinitions.add(columnDefinitionBuilder.toString());
            }

            // Add primary key constraint
            List<String> primaryKeyColumns = new ArrayList<>();
            while (primaryKeyResultSet.next()) {
                String columnName = primaryKeyResultSet.getString("COLUMN_NAME");
                primaryKeyColumns.add(columnName);
            }
            if (!primaryKeyColumns.isEmpty()) {
                columnDefinitions.add("PRIMARY KEY (" + String.join(", ", primaryKeyColumns) + ")");
            }

            // Add foreign key constraints
            while (foreignKeyResultSet.next()) {
                String columnName = foreignKeyResultSet.getString("FKCOLUMN_NAME");
                String referencedTableName = foreignKeyResultSet.getString("PKTABLE_NAME");
                String referencedColumnName = foreignKeyResultSet.getString("PKCOLUMN_NAME");
                columnDefinitions.add("FOREIGN KEY (" + columnName + ") REFERENCES " + referencedTableName +
                                              "(" + referencedColumnName + ")");
            }

            // Add index definitions
            while (indexResultSet.next()) {
                String indexName = indexResultSet.getString("INDEX_NAME");
                String columnName = indexResultSet.getString("COLUMN_NAME");
                boolean nonUnique = indexResultSet.getBoolean("NON_UNIQUE");

                if (!indexDefinitions.containsKey(indexName)) {
                    String indexDefinition = (nonUnique ? "INDEX " : "UNIQUE INDEX ") + indexName + " (" + columnName + ")";
                    indexDefinitions.put(indexName, indexDefinition);
                    columnDefinitions.add(indexDefinition);
                }
            }

            statementBuilder.append(String.join(", ", columnDefinitions));
            statementBuilder.append(")");
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(statementBuilder.toString(), "mysql");
            SQLStatement sqlStatement = parser.parseStatement();
            createTableStatements.add(sqlStatement.toString());
        }

        resultSet.close();

        return createTableStatements;
    }

    private static List<String> generateInsertDataStatements(DruidDataSource dataSource) throws SQLException {
        List<String> statements = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(connection.getCatalog(), null, null, new String[]{"TABLE"});

        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            ResultSet dataResultSet = connection.createStatement().executeQuery("SELECT * FROM " + tableName);

            while (dataResultSet.next()) {
                StringBuilder statementBuilder = new StringBuilder();
                statementBuilder.append("INSERT INTO ").append(tableName).append(" VALUES (");

                int columnCount = dataResultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    Object value = dataResultSet.getObject(i);
                    if (value != null) {
                        statementBuilder.append("'").append(value).append("'");
                    } else {
                        statementBuilder.append("NULL");
                    }

                    if (i < columnCount) {
                        statementBuilder.append(",");
                    }
                }

                statementBuilder.append(");");
                statements.add(statementBuilder.toString());
            }

            dataResultSet.close();
        }

        resultSet.close();
        connection.close();

        return statements;
    }

}
