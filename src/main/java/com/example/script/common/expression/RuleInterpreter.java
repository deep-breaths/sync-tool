package com.example.script.common.expression;

/**
 * @author albert lewis
 * @date 2023/12/29
 */
public class RuleInterpreter {
    private final RuleExpression expression;

    public RuleInterpreter(RuleExpression expression) {
        this.expression = expression;
    }

    public void interpret(Context context) {
        expression.interpret(context);
    }
}
