package com.example.script.common.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 数据库导出规则，默认导出全部表结构
 * @author albert lewis
 * @date 2024/1/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DBRule {

    /**
     * 数据库名
     */
    private String name;

    /**
     * 是否导出该数据库
     */
    private Boolean isInclude=false;
    /**
     * 是否导出数据
     */
    private Boolean includeData=false;
    /**
     * 是否导出全部数据
     */
    private Boolean isAllData=false;
    /**
     * 表数据规则
     */
    private Map<String,TableRule> tableRule;
    /**
     * 是否是多租户库
     */
    private Boolean isMultiTenant=false;

    public DBRule(String name) {
        this.name = name;
    }
}
