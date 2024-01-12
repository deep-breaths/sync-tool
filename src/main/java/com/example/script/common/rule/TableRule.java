package com.example.script.common.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author albert lewis
 * @date 2024/1/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TableRule {

    /**
     *表名
     */
    private String name;
    /**
     * 单表数据范围
     */
    private String where;

    /**
     * 单表数据修改语句
     */
    private List<String> update;
    /**
     * 是否全部数据
     */
    private Boolean isAll=true;
    /**
     * 该数据是否是多租户
     */
    private Boolean isMultiTenant=false;

    public TableRule(String name,Boolean isMultiTenant) {
        this.isMultiTenant=isMultiTenant;
        this.name = name;
    }
}
