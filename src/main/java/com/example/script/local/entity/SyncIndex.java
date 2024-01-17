package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 存储索引信息的表
 *
 * @TableName sync_index
 */
@TableName(value = "sync_index")
@Data
public class SyncIndex implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 版本号
     */
    private Long versionId;

    /**
     * 数据库id
     */
    private Long dbId;

    /**
     * 表id
     */
    private Long tableId;

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
    private String compositeCol;

    /**
     * 是否不是唯一
     */
    private Integer nonUnique;

    /**
     * 索引类型
     */
    private String type;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}