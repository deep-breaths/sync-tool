package com.example.script.common.rule;

import lombok.Data;

import java.util.List;

/**
 * @author albert lewis
 * @date 2024/1/12
 */
@Data
public class ExportDataRule {

    /**
     * 是否导出数据
     */
    private Boolean includeData=true;
    /**
     * 单表数据范围
     */
    private String where;

    /**
     * 单表数据修改语句
     */
    private List<String> update;
}
