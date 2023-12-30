package com.example.script.common.domain;

import lombok.Data;

import java.util.Set;

/**
 * @author albert lewis
 * @date 2023/12/30
 */
@Data
public class TableIndex {
    /**
     * 索引名称
     */
    private String indexName;

    /**
     * 复合列
     */
    private Set<String> compositeCol;

    /**
     * 是否唯一
     */
    private Boolean nonUnique;
    /**
     * 类型
     */
    private String type;
}
