package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 存储外键信息的表
 * @TableName sync_fk
 */
@TableName(value ="sync_fk")
@Data
public class SyncFk implements Serializable {
    /**
     * ID
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
    private String fkColName;

    /**
     * 关联的主键表名
     */
    private String pkTableName;

    /**
     * 关联的主键列名
     */
    private String pkColName;

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