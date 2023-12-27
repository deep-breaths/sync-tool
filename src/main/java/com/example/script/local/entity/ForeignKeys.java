package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 存储外键信息的表
 *
 * @TableName foreign_keys
 */
@TableName(value = "foreign_keys")
@Data
public class ForeignKeys implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 版本号
     */
    private Integer versionId;

    /**
     * 表id
     */
    private Integer tableId;

    /**
     * 外键名称
     */
    private String fkName;

    /**
     * 外键表名
     */
    private String fkTableName;

    /**
     * 外键列名
     */
    private String fkColumnName;

    /**
     * 关联的主键表名
     */
    private String pkTableName;

    /**
     * 关联的主键列名
     */
    private String pkColumnName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}