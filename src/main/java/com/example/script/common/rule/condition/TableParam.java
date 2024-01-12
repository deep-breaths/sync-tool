package com.example.script.common.rule.condition;

import com.example.script.common.rule.DBRule;
import com.example.script.common.rule.TableRule;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;

/**
 * @author albert lewis
 * @date 2024/1/12
 */
@AllArgsConstructor
public class TableParam {

    private DBRule currentDBRule;
    private TableRule currentTableRule;

    public TableParam name(String tableName) {
        this.currentTableRule = new TableRule(tableName,currentDBRule.getIsMultiTenant());
        this.currentDBRule.getTableRule().put(tableName, currentTableRule);
        return this;
    }

    public TableParam name(String tableName, Consumer<Some> someConsumer) {
        name(tableName);
        Some some = new Some(currentTableRule);
        someConsumer.accept(some);
        return this;
    }
}
