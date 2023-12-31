package com.example.script.common.domain;

import lombok.Data;

import java.util.List;

/**
 * @author albert lewis
 * @date 2023/12/31
 */
@Data
public class Database {
    private String name;

    private Integer status;

    private List<TableStatus> tableStatus;
    private List<TableColumnStatus> tableColumnStatus;




}
