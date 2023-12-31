package com.example.script.common.domain;

import lombok.Data;

import java.util.List;

/**
 * @author albert lewis
 * @date 2023/12/31
 */
@Data
public class TableColumnStatus {

    private String tableName;
    private String name;

    private Integer status;

   List<String> insert;
   List<String> update;
   List<String> delete;
}
