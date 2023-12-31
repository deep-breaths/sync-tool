package com.example.script.common.domain;

import lombok.Data;

import java.util.List;

/**
 * @author albert lewis
 * @date 2023/12/31
 */
@Data
public class TableStatus {

    private String name;

    private Integer status;

    String create;
    List<String> alert;
    List<String> drop;
}
