package com.example.script.product.mysql.strategy;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLIndexDefinition;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import com.example.script.common.domain.TableInfo;
import com.example.script.common.entity.*;
import com.example.script.common.rule.ExportDataRule;
import com.example.script.common.rule.RuleUtils;
import com.example.script.common.strategy.DataSourceStrategy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@Component
@Scope("prototype")
public class MySqlStrategy extends DataSourceStrategy {
    @Override
    public String getName() {
        return "mysql";
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
    public TableInfo parsingTableStatement(SyncTable syncTable) {

        String tableDDL=syncTable.getTableStatement();
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(tableDDL, JdbcConstants.MYSQL);
        List<SQLStatement> statements = parser.parseStatementList();
        SQLStatement statement = statements.getFirst();
        if (statement instanceof MySqlCreateTableStatement createTableStatement) {
            String tableName = replace(createTableStatement.getTableName());
            syncTable.setName(tableName);
            syncTable.setRemarks(createTableStatement.getComment() == null ? null : createTableStatement
                    .getComment()
                    .toString());
            List<SQLTableElement> tableElementList = createTableStatement.getTableElementList();
//            Set<String> pkList = new LinkedHashSet<>();
            List<SyncIndex> indexList = new ArrayList<>();
            List<SyncFk> fkList = new ArrayList<>();
            List<SyncCol> colList = new ArrayList<>();
            for (int i = 0; i < tableElementList.size(); i++) {
                SQLTableElement sqlTableElement = tableElementList.get(i);
                if (sqlTableElement instanceof SQLColumnDefinition tableElement) {
                    SyncCol tableColumn = new SyncCol();
                    tableColumn.setDbId(syncTable.getDbId()).setTableId(syncTable.getId()).setDbName(syncTable.getDbName()).setTableName(tableName);
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
                    colList.add(tableColumn);


                } else if (sqlTableElement instanceof MysqlForeignKey tableElement) {
                    SyncFk tableFK = new SyncFk();
                    tableFK.setDbId(syncTable.getDbId()).setTableId(syncTable.getId()).setDbName(syncTable.getDbName()).setTableName(tableName);
                    tableFK.setFkName(replace(tableElement.getName().getSimpleName()));
                    tableFK.setFkTableName(tableName);
                    tableFK.setFkColName(replace(tableElement.getReferencingColumns().getFirst().getSimpleName()));
                    tableFK.setPkTableName(replace(tableElement.getReferencedTableName().getSimpleName()));
                    tableFK.setPkColName(replace(tableElement.getReferencedColumns().getFirst().getSimpleName()));
                    fkList.add(tableFK);
                } else if (sqlTableElement instanceof MySqlPrimaryKey tableElement) {
                    SQLIndexDefinition indexDefinition = tableElement
                            .getIndexDefinition();
                    indexDefinition
                            .getColumns()
                            .forEach(x -> {
                                String columnName = replace(x.getExpr().toString());
                                SyncIndex tableIndex = new SyncIndex();
                                tableIndex.setDbId(syncTable.getDbId()).setTableId(syncTable.getId()).setDbName(syncTable.getDbName()).setTableName(tableName);
                                tableIndex.setCompositeCol(List.of(columnName));
                                tableIndex.setNonUnique(Boolean.FALSE);
                                tableIndex.setIndexName(replace(indexDefinition.getName() == null ? null : indexDefinition
                                        .getName()
                                        .toString()));
                                tableIndex.setType(indexDefinition.getType());
                                indexList.add(tableIndex);
                            });


                } else if (sqlTableElement instanceof MySqlUnique tableElement) {
                    SQLIndexDefinition indexDefinition = tableElement
                            .getIndexDefinition();
                    List<String> columnNames = indexDefinition
                            .getColumns()
                            .stream().map(x -> replace(x.getExpr().toString()))
                            .toList();
                    SyncIndex tableIndex = new SyncIndex();
                    tableIndex.setDbId(syncTable.getDbId()).setTableId(syncTable.getId()).setDbName(syncTable.getDbName()).setTableName(tableName);
                    tableIndex.setCompositeCol(columnNames);
                    tableIndex.setNonUnique(Boolean.FALSE);
                    tableIndex.setIndexName(replace(indexDefinition.getName() == null ? null : indexDefinition
                            .getName()
                            .toString()));
                    tableIndex.setType(indexDefinition.getType());
                    indexList.add(tableIndex);
                } else if (sqlTableElement instanceof MySqlKey tableElement) {
                    SQLIndexDefinition indexDefinition = tableElement
                            .getIndexDefinition();
                    List<String> columnNames = indexDefinition
                            .getColumns()
                            .stream().map(x -> replace(x.getExpr().toString()))
                            .toList();
                    SyncIndex tableIndex = new SyncIndex();
                    tableIndex.setDbId(syncTable.getDbId()).setTableId(syncTable.getId()).setDbName(syncTable.getDbName()).setTableName(tableName);

                    tableIndex.setCompositeCol(columnNames);
                    tableIndex.setNonUnique(Boolean.TRUE);
                    tableIndex.setIndexName(replace(indexDefinition.getName() == null ? null : indexDefinition
                            .getName()
                            .toString()));
                    tableIndex.setType(indexDefinition.getType());
                    indexList.add(tableIndex);
                }
            }
            syncTable.setTableStatement(tableDDL);
            TableInfo tableInfo = new TableInfo();
            tableInfo.setFkList(fkList);
            tableInfo.setIndexList(indexList);
            tableInfo.setColList(colList);
            List<String> tableOptions = createTableStatement
                    .getTableOptions()
                    .stream()
                    .map(x -> String.format("%s = %s",
                                            x.getTarget().toString(), x.getValue().toString()
                    ))
                    .toList();
            syncTable.setTableOptions(tableOptions);
            return tableInfo.setTableInfo(syncTable);
        }
        return null;
    }

    @Override
    public List<SyncData> getTableData(SyncTable syncTable) throws SQLException {
        String databaseName = syncTable.getDbName();
        String tableName = syncTable.getName();
        ExportDataRule exportDataRule = RuleUtils.getTableDataCondition(databaseName, tableName);
        if (!exportDataRule.getIncludeData()) {
            return new ArrayList<>();
        }
        String where = exportDataRule.getWhere();
        String selectSql = String.format("SELECT * FROM `%s`.`%s`", databaseName, tableName);
        if (where != null && !where.isBlank()) {
            if (where.startsWith("limit")) {
                selectSql = String.format("%s %s", selectSql, where);
            } else {
                selectSql = String.format("%s WHERE %s", selectSql, where);
            }

        }
        ResultSet dataResultSet = super
                .getConn()
                .createStatement()
                .executeQuery(selectSql);
        ResultSetMetaData resultSetMetaData = dataResultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        int rowNum = 0;
        List<SyncData> rows = new ArrayList<>();
        while (dataResultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                SyncData tableData = new SyncData();
                tableData.setDbId(syncTable.getDbId()).setTableId(syncTable.getId()).setDbName(syncTable.getDbName()).setTableName(tableName);
                tableData.setColName(resultSetMetaData.getColumnName(i));
                tableData.setColValue(dataResultSet.getObject(i) == null ? null : String.valueOf(dataResultSet.getObject(i)));
                tableData.setRowNum(rowNum);
                rows.add(tableData);
            }
            rowNum++;
        }
        return rows;
    }


    private String replace(String name) {
        if (name == null) {
            return null;
        }
        return name.replace("`", "");
    }

}
