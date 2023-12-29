package com.example.script.test.domain;

import java.util.List;

/**
 * @author albert lewis
 * @date 2023/12/25
 */
public class DiffDML {
    private List<String> inserts;
    private List<String> updates;
    private List<String> deletes;

    public List<String> getInserts() {
        return inserts;
    }

    public void setInserts(List<String> inserts) {
        this.inserts = inserts;
    }

    public List<String> getUpdates() {
        return updates;
    }

    public void setUpdates(List<String> updates) {
        this.updates = updates;
    }

    public List<String> getDeletes() {
        return deletes;
    }

    public void setDeletes(List<String> deletes) {
        this.deletes = deletes;
    }
}
