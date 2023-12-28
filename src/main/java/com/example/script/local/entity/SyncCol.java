package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 表字段信息(来源于建表语句的拆解)
 * @TableName sync_col
 */
@TableName(value ="sync_col")
@Data
@Builder
public class SyncCol implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    private String isNullable;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 修改时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}