package com.example.script.common.rule;

import com.example.script.common.rule.condition.DBParam;
import com.example.script.common.rule.condition.TableParam;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author albert lewis
 * @date 2024/1/3
 */
public class BackupRule {
    private String currentDB;
    private DBRule currentDBRule;
    private TableRule currentTableRule;
    private Map<String, DBRule> ruleMap;

    public Map<String, DBRule> getRuleMap() {
        return ruleMap;
    }

    public BackupRule() {
        this.ruleMap = new LinkedHashMap<>();
    }

    public BackupRule buildDB(String dbName) {
        this.currentDB = dbName;
        this.currentDBRule = new DBRule(currentDB);
        this.currentDBRule.setIsInclude(true);
        this.currentDBRule.setTableRule(new LinkedHashMap<>());
        this.ruleMap.put(currentDB, currentDBRule);
        return this;
    }

    public BackupRule buildDB(String dbName, Consumer<DBParam> dbConsumer) {
        buildDB(dbName);
        DBParam dbParam = new DBParam(this.currentDBRule);
        dbConsumer.accept(dbParam);
        return this;
    }

    public BackupRule buildDB(String dbName, Consumer<DBParam> dbConsumer, Consumer<TableParam> tableConsumer) {
        buildDB(dbName);
        DBParam dbParam = new DBParam(this.currentDBRule);
        TableParam tableParam = new TableParam(this.currentDBRule, this.currentTableRule);
        dbConsumer.accept(dbParam);
        tableConsumer.accept(tableParam);
        return this;
    }


    public BackupRule build() {
        return this;
    }


}
