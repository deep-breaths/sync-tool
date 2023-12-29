package com.example.script.common.expression;

import java.util.Map;

/**
 * @author albert lewis
 * @date 2023/12/29
 */
public class Context {
    private final Map<String, Map<String, String>> backupRules;

    public Context(Map<String, Map<String, String>> backupRules) {
        this.backupRules = backupRules;
    }

    public void backupData(String dataName) {
        System.out.println("备份数据：" + dataName);
    }

    public void backupTable(String tableName) {
        System.out.println("备份表：" + tableName);
    }

    public void backupDataTable(String tableName, String dataName) {
        System.out.println("备份数据表：" + tableName + "，数据：" + dataName);
    }

    public Map<String, Map<String, String>> getBackupRules() {
        return backupRules;
    }
}
