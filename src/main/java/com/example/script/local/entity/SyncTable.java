package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName sync_table
 */
@TableName(value ="sync_table")
@Data
public class SyncTable implements Serializable {
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
     * 数据库id
     */
    private Long dbId;

    /**
     * 表名
     *
     */
    private String name;

    /**
     * 建表语句
     */
    private String tableInfo;

    /**
     * 索引
     */
    private String indexId;

    /**
     * 外键
     */
    private String fkId;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 表类型，table、view
     *
    private String type;

    /**
     * 主键
    private String pk;

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