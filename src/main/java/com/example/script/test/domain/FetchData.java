package com.example.script.test.domain;

import java.util.Map;

/**
 * @author albert lewis
 * @date 2023/12/25
 */
public class FetchData {

    private String tableName;

    private Map<Map<String, Object>, Map<String, Object>>  data;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<Map<String, Object>, Map<String, Object>> getData() {
        return data;
    }

    public void setData(Map<Map<String, Object>, Map<String, Object>> data) {
        this.data = data;
    }
}
