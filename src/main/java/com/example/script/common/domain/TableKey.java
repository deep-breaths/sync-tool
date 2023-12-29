package com.example.script.common.domain;

import lombok.Data;

import java.util.Set;

/**
 * @author albert lewis
 * @date 2023/12/25
 */
@Data
public class TableKey {
    private String tableName;

    private Set<Key> keys;

}
