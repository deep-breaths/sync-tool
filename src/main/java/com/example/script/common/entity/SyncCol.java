package com.example.script.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 表字段信息(来源于建表语句的拆解)
 * @TableName sync_col
 */
@TableName(value ="sync_col")
@Data
@Accessors(chain = true)
public class SyncCol implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 版本id
     */
    private Long versionId;

    /**
     * 表id
     */
    private Long tableId;

    /**
     * 数据库id
     */
    private Long dbId;
    private String dbName;
    private String tableName;
    /**
     * 字段名
     */
    private String name;


    /**
     * 字段类型
     */
    private String type;

    /**
     * 长度
     */
    private Long size;

    /**
     * 是否为null
     */
    private Boolean isNullable;

    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 上一个字段
     */
    private String lastColumn;

    /**
     * 备注
     */
    private String remarks;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}