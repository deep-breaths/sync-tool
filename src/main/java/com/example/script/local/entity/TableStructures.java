package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName table_structures
 */
@TableName(value = "table_structures")
@Data
public class TableStructures implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 版本id
     */
    private Integer versionId;

    /**
     * 数据库id
     */
    private Integer databaseId;

    /**
     * 表名
     */
    private String name;

    /**
     * 建表语句
     */
    private String structure;

    /**
     * 索引
     */
    private String indexesId;

    /**
     * 外键
     */
    private String foreignKeysId;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 表类型，table、view
     */
    private String type;

    /**
     * 主键
     */
    private String primaryKeys;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}