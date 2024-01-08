package com.example.script.common.rule;

import lombok.Data;

/**
 * @author albert lewis
 * @date 2024/1/3
 */
@Data
public class TableRule {
    private String where;
    private Boolean isAll;

    private Boolean isMultiTenant;
}
