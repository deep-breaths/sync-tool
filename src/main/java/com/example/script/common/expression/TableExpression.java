package com.example.script.common.expression;

/**
 * @author albert lewis
 * @date 2023/12/29
 */
public class TableExpression implements RuleExpression {
    private final String tableName;

    public TableExpression(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void interpret(Context context) {
        // 执行备份表的操作
        context.backupTable(tableName);
    }
}
