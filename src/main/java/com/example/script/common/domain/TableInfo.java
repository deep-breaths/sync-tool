package com.example.script.common.domain;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author albert lewis
 * @date 2023/12/30
 */
@Data
public class TableInfo {
    private String name;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 表类型，table、view
     */
    private String type;

//    private Set<String> pkList;

    /**
     * 索引
     */
    private Set<TableIndex> indexList;

    /**
     * 外键
     */
    private Set<TableFK> fkList;

    private Set<TableColumn> columnInfoList;

    /**
     * 建表语句
     */
    private String tableStatement;
    /**
     * 编码等 ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC
     */
    private List<String> tableOptions;
}
