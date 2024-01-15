package com.example.script.product.mysql.strategy;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLIndexDefinition;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.example.script.common.domain.*;
import com.example.script.common.rule.ExportDataRule;
import com.example.script.common.rule.RuleUtils;
import com.example.script.common.strategy.DataBaseStrategy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@Component
@Scope("prototype")
public class MySqlStrategy extends DataBaseStrategy {
    @Override
    public String getName() {
        return "mysql";
    }


    @Override
    public List<Map<String, Object>> toGetDataByTable(String databaseName, String tableName) throws SQLException {
        ExportDataRule exportDataRule = RuleUtils.getTableDataCondition(databaseName, tableName);
        if (!exportDataRule.getIncludeData()){
            return new ArrayList<>();
        }
        String where = exportDataRule.getWhere();
        List<Map<String, Object>> rows = new ArrayList<>();
        String selectSql = String.format("SELECT * FROM `%s`.`%s`", databaseName, tableName);
        if (where!=null&&!where.isBlank()){
            if (where.contains("limit")){
                selectSql=String.format("%s %s",selectSql,where);
            }else {
                selectSql=String.format("%s WHERE %s",selectSql,where);
            }

        }
        ResultSet dataResultSet = super
                .getConn()
                .createStatement()
                .executeQuery(selectSql);
        ResultSetMetaData resultSetMetaData = dataResultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        while (dataResultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(resultSetMetaData.getColumnName(i), dataResultSet.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }


    @Override
    public String getTableStructure(String databaseName, String tableName) throws SQLException {
        String sql = String.format(" SHOW CREATE TABLE `%s`.`%s`", databaseName, tableName);
        ResultSet resultSet = getConn().createStatement().executeQuery(sql);
        if (resultSet.next()) {
            return resultSet.getString(2);
        }
        resultSet.close();
        return null;
    }

    @Override
    public TableInfo parsingTableStatement(String tableDDL) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(tableDDL, JdbcConstants.MYSQL);
        List<SQLStatement> statements = parser.parseStatementList();
        SQLStatement statement = statements.getFirst();
        if (statement instanceof MySqlCreateTableStatement createTableStatement) {
            TableInfo tableInfo = new TableInfo();
            String tableName = replace(createTableStatement.getTableName());
            tableInfo.setName(tableName);
            tableInfo.setRemarks(createTableStatement.getComment() == null ? null : createTableStatement
                    .getComment()
                    .toString());
            List<SQLTableElement> tableElementList = createTableStatement.getTableElementList();
            Set<String> pkList = new LinkedHashSet<>();
            Set<TableIndex> indexList =new LinkedHashSet<>();
            Set<TableFK> fkList =new LinkedHashSet<>();
            Set<TableColumn> columnInfoList =new LinkedHashSet<>();
            for (int i = 0; i < tableElementList.size(); i++) {
                SQLTableElement sqlTableElement = tableElementList.get(i);
                if (sqlTableElement instanceof SQLColumnDefinition tableElement) {
                    TableColumn tableColumn = new TableColumn();
                    if (i != 0) {
                        SQLColumnDefinition lastColumn = (SQLColumnDefinition) tableElementList.get(i - 1);
                        tableColumn.setLastColumn(replace(lastColumn.getColumnName()));
                    }
                    tableColumn.setRemarks(tableElement.getComment() == null ? null : tableElement
                            .getComment()
                            .toString());
                    tableColumn.setIsNullable(true);
                    tableColumn.setDefaultValue(tableElement.getDefaultExpr() == null ? null :
                                                        tableElement.getDefaultExpr().toString());
                    SQLDataType dataType = tableElement.getDataType();
                    tableColumn.setType(dataType.getName());
                    tableColumn.setSize(dataType.getArguments().isEmpty() ? null :
                                                Long.valueOf(dataType.getArguments().getFirst().toString()));
                    tableColumn.setName(replace(tableElement.getColumnName()));
                    columnInfoList.add(tableColumn);


                } else if (sqlTableElement instanceof MysqlForeignKey tableElement) {
                    TableFK tableFK = new TableFK();
                    tableFK.setFkName(replace(tableElement.getName().getSimpleName()));
                    tableFK.setFkTableName(tableName);
                    tableFK.setFkColName(replace(tableElement.getReferencingColumns().getFirst().getSimpleName()));
                    tableFK.setPkTableName(replace(tableElement.getReferencedTableName().getSimpleName()));
                    tableFK.setPkColName(replace(tableElement.getReferencedColumns().getFirst().getSimpleName()));
                    fkList.add(tableFK);
                } else if (sqlTableElement instanceof MySqlPrimaryKey tableElement) {
                    SQLIndexDefinition indexDefinition = tableElement
                            .getIndexDefinition();
                    pkList = indexDefinition
                            .getColumns()
                            .stream().map(x -> replace(x.getExpr().toString()))
                            .collect(Collectors.toSet());

                } else if (sqlTableElement instanceof MySqlUnique tableElement) {
                    SQLIndexDefinition indexDefinition = tableElement
                            .getIndexDefinition();
                    Set<String> columnNames = indexDefinition
                            .getColumns()
                            .stream().map(x -> replace(x.getExpr().toString()))
                            .collect(Collectors.toSet());
                    TableIndex tableIndex = new TableIndex();
                    tableIndex.setCompositeCol(columnNames);
                    tableIndex.setNonUnique(Boolean.FALSE);
                    tableIndex.setIndexName(replace(indexDefinition.getName()==null?null:indexDefinition.getName().toString()));
                    tableIndex.setType(indexDefinition.getType());
                    indexList.add(tableIndex);
                } else if (sqlTableElement instanceof MySqlKey tableElement) {
                    SQLIndexDefinition indexDefinition = tableElement
                            .getIndexDefinition();
                    Set<String> columnNames = indexDefinition
                            .getColumns()
                            .stream().map(x -> replace(x.getExpr().toString()))
                            .collect(Collectors.toSet());
                    TableIndex tableIndex = new TableIndex();
                    tableIndex.setCompositeCol(columnNames);
                    tableIndex.setNonUnique(Boolean.TRUE);
                    tableIndex.setIndexName(replace(indexDefinition.getName()==null?null:indexDefinition.getName().toString()));
                    tableIndex.setType(indexDefinition.getType());
                    indexList.add(tableIndex);
                }
            }
            tableInfo.setTableStatement(tableDDL);
            tableInfo.setFkList(fkList);
            tableInfo.setPkList(pkList);
            tableInfo.setIndexList(indexList);
            tableInfo.setColumnInfoList(columnInfoList);
            List<String> tableOptions = createTableStatement.getTableOptions().stream().map(x -> String.format("%s = %s",
                                                                                                       x.getTarget().toString(), x.getValue().toString()
            )).toList();
            tableInfo.setTableOptions(tableOptions);
            return tableInfo;
        }
        return null;
    }

    private String replace(String name) {
        if (name == null) {
            return null;
        }
        return name.replace("`", "");
    }

    @Override
    public TableKey getPrimaryOrUniqueKeys(String tableDDL) {
        Set<Key> keys =new LinkedHashSet<>();
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
                    Key theKey = new Key();
                    SQLIndexDefinition indexDefinition = key
                            .getIndexDefinition();
                    Set<String> list = indexDefinition
                            .getColumns()
                            .stream().map(x -> x.getExpr().toString().replace("`", ""))
                            .collect(Collectors.toSet());

                    if (key instanceof MySqlPrimaryKey) {
                        theKey.setIsOnly(true);

                    } else if (key instanceof MySqlUnique) {
                        theKey.setIsOnly(true);
                    } else {
                        theKey.setIsOnly(false);
                    }
                    theKey.setType(indexDefinition.getType());
                    SQLName name = indexDefinition.getName();
                    theKey.setName(name == null ? null : name.toString().replace("`", ""));
                    theKey.setColumns(list);
                    keys.add(theKey);


                }


            }
        }
        TableKey tableKey = new TableKey();
        tableKey.setTableName(tableName);
        tableKey.setKeys(keys);
        return tableKey;
    }

}
