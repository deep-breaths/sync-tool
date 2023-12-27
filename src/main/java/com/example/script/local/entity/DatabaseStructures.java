package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据库列表
 *
 * @TableName database_structures
 */
@TableName(value = "database_structures")
@Data
public class DatabaseStructures implements Serializable {
    /**
     *
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 版本id
     */
    private Integer versionId;

    /**
     * 数据库名
     */
    private String schemaName;

    /**
     * 建库语句
     */
    private String structure;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}