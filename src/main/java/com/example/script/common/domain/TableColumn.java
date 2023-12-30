package com.example.script.common.domain;

import lombok.Data;

/**
 * @author albert lewis
 * @date 2023/12/30
 */
@Data
public class TableColumn {
    /**
     * 字段名
     */
    private String name;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 长度
     */
    private Long size;

    /**
     * 是否为null
     */
    private Boolean isNullable;

    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 上一个字段
     */
    private String lastColumn;

    /**
     * 备注
     */
    private String remarks;
}
