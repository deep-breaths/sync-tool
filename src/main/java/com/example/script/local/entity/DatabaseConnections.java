package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据库连接信息
 *
 * @TableName database_connections
 */
@TableName(value = "database_connections")
@Data
public class DatabaseConnections implements Serializable {
    /**
     * 连接id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 连接名称，用于标识连接
     */
    private String connectionName;

    /**
     * 数据库类型，如 MySQL、PostgreSQL 等
     */
    private Integer dbTypeId;

    /**
     * 主机名或IP地址
     */
    private String host;

    /**
     * 数据库端口
     */
    private String port;

    /**
     * 数据库用户名
     */
    private String username;

    /**
     * 数据库密码
     */
    private String password;

    /**
     * 备注
     */
    private String remarks;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}