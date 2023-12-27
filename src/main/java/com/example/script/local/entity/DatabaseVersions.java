package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据库版本
 *
 * @TableName database_versions
 */
@TableName(value = "database_versions")
@Data
public class DatabaseVersions implements Serializable {
    /**
     * 版本ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 外键，DatabaseConnections
     */
    private Integer connectionId;

    /**
     * 版本号
     */
    private Integer versionNumber;

    /**
     * 版本描述
     */
    private String remarks;

    /**
     * 应用的时间戳
     */
    private LocalDateTime appliedOn;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}