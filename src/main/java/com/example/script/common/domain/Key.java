package com.example.script.common.domain;

import lombok.Data;

import java.util.Set;

/**
 * @author albert lewis
 * @date 2023/12/29
 */
@Data
public class Key {
    private String name;

    private Boolean isOnly;
    private String type;

    private Set<String> columns;
}
