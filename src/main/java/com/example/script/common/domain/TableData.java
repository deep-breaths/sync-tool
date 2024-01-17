package com.example.script.common.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 
 * @TableName sync_data
 */

@Data
public class TableData {

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 初始化insert语句
     */
    private String dataInfo;

    /**
     * 行号
     */
    private Integer rowNum;
    /**
     * 列名
     */
    private String colName;
    /**
     * 列值
     */
    private String colValue;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}