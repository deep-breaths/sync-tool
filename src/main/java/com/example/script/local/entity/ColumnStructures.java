package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 表字段信息
 *
 * @TableName column_structures
 */
@TableName(value = "column_structures")
@Data
@Builder
public class ColumnStructures implements Serializable {
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
     * 表id
     */
    private Integer tableId;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}