package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 存储索引信息的表
 *
 * @TableName indexes
 */
@TableName(value = "indexes")
@Data
public class Indexes implements Serializable {
    /**
     *
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
     * 表名
     */
    private String tableName;

    /**
     * 索引名称
     */
    private String indexName;

    /**
     * 复合列
     */
    private String compositeColumns;

    /**
     * 是否唯一
     */
    private Integer nonUnique;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}