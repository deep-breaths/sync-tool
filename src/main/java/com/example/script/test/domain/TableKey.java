package com.example.script.test.domain;

import java.util.Set;

/**
 * @author albert lewis
 * @date 2023/12/25
 */
public class TableKey {
    private String tableName;

    private Set<String> keys;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Set<String> getKeys() {
        return keys;
    }

    public void setKeys(Set<String> keys) {
        this.keys = keys;
    }
}
