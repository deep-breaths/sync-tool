package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName sync_data
 */
@TableName(value ="sync_data")
@Data
public class SyncData implements Serializable {
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
     * 初始化insert语句
     */
    private String dataInfo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}