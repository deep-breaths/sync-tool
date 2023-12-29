package com.example.script.common.expression;

/**
 * @author albert lewis
 * @date 2023/12/29
 */
public class DataTableExpression implements RuleExpression {
    private final String tableName;
    private final String dataName;

    public DataTableExpression(String tableName, String dataName) {
        this.tableName = tableName;
        this.dataName = dataName;
    }

    @Override
    public void interpret(Context context) {
        // 执行备份数据表的操作
        context.backupDataTable(tableName, dataName);
    }
}