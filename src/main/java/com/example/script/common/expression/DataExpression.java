package com.example.script.common.expression;

/**
 * @author albert lewis
 * @date 2023/12/29
 */
public class DataExpression implements RuleExpression {
    private final String dataName;

    public DataExpression(String dataName) {
        this.dataName = dataName;
    }

    @Override
    public void interpret(Context context) {
        // 执行备份数据的操作
        context.backupData(dataName);
    }
}
