package com.example.script.common.rule.condition;

import com.example.script.common.rule.DBRule;
import lombok.AllArgsConstructor;

/**
 * @author albert lewis
 * @date 2024/1/12
 */
@AllArgsConstructor
public class DBParam {
    private DBRule currentDBRule;

    public DBParam isInclude(boolean isInclude){
        this.currentDBRule.setIsInclude(isInclude);
        return this;
    }

    public DBParam isInclude(){
        isInclude(Boolean.TRUE);
        return this;
    }
    public void notInclude(){
        isInclude(Boolean.FALSE);
    }
    public DBParam includeData(boolean includeData){
        this.currentDBRule.setIncludeData(includeData);
        return this;
    }
    public DBParam includeData(){
        isInclude();
        includeData(Boolean.TRUE);
        return this;
    }
    public DBParam notIncludeData(){
        includeData(Boolean.FALSE);
        return this;
    }
    public DBParam isAllData(boolean isAllData){
        this.currentDBRule.setIsAllData(isAllData);
        return this;
    }

    public DBParam isAllData(){
        includeData();
        isAllData(Boolean.TRUE);
        return this;
    }
    public DBParam notIsAllData(){
        includeData();
        isAllData(Boolean.FALSE);
        return this;
    }

    public DBParam isMultiTenant(boolean isMultiTenant){
        this.currentDBRule.setIsMultiTenant(isMultiTenant);
        return this;
    }

    public DBParam isMultiTenant(){
        isMultiTenant(Boolean.TRUE);
        return this;
    }

    public DBParam notMultiTenant(){
        isMultiTenant(Boolean.FALSE);
        return this;
    }

    public DBParam isAllStruct(Boolean isAllStruct){
        this.currentDBRule.setIsAllStruct(isAllStruct);
        return this;
    }

    public DBParam isAllStruct(){
        isAllStruct(Boolean.TRUE);
        return this;
    }
    public DBParam notIsAllStruct(){
        isAllStruct(Boolean.FALSE);
        return this;
    }
}
