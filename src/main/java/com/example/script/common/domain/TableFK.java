package com.example.script.common.domain;

import lombok.Data;

/**
 * @author albert lewis
 * @date 2023/12/30
 */
@Data
public class TableFK {
    /**
     * 外键名称
     */
    private String fkName;

    /**
     * 外键表名
     */
    private String fkTableName;

    /**
     * 外键列名
     */
    private String fkColName;

    /**
     * 关联的主键表名
     */
    private String pkTableName;

    /**
     * 关联的主键列名
     */
    private String pkColName;
}
