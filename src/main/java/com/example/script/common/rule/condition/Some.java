package com.example.script.common.rule.condition;

import com.example.script.common.rule.TableRule;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author albert lewis
 * @date 2024/1/12
 */
@AllArgsConstructor
public class Some {
    private TableRule currentTableRule;

    public Some where(String condition) {
        notAllData();
        includeData();
        currentTableRule.setWhere(condition);
        return this;
    }

    public Some update(List<String> updates) {
        currentTableRule.setUpdate(updates);
        return this;
    }



    public Some includeData(boolean includeData) {
        currentTableRule.setIncludeData(includeData);
        return this;
    }
    public Some includeData() {
        includeData(Boolean.TRUE);
        return this;
    }
    public Some notIncludeData() {
        includeData(Boolean.FALSE);
        return this;
    }
    public Some isAllData(boolean isAllData) {
        currentTableRule.setIsAllData(isAllData);
        return this;
    }
    public Some isAllData() {
        includeData();
        isAllData(Boolean.TRUE);
        return this;
    }
    public Some notAllData() {
        isAllData(Boolean.FALSE);
        return this;
    }

    public Some isMultiTenant(boolean isMultiTenant) {
        currentTableRule.setIsMultiTenant(isMultiTenant);
        return this;
    }

    public Some isMultiTenant() {
        isMultiTenant(Boolean.TRUE);
        return this;
    }
    public Some notMultiTenant() {
        isMultiTenant(Boolean.FALSE);
        return this;
    }
}
