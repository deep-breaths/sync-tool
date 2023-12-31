package com.example.script.domain;

import java.util.Map;
import java.util.Set;

/**
 * @author albert lewis
 * @date 2023/12/25
 */
public class TableKey {
    private String tableName;

    private Map<String,Set<String>> keys;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public  Map<String,Set<String>> getKeys() {
        return keys;
    }

    public void setKeys( Map<String,Set<String>> keys) {
        this.keys = keys;
    }
}
