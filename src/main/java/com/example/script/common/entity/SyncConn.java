package com.example.script.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.script.common.domain.CommonModel;
import lombok.Data;

/**
 * 数据库连接信息
 * @TableName sync_conn
 */
@TableName(value ="sync_conn")
@Data
public class SyncConn extends CommonModel<SyncConn> {
    /**
     * 连接id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 连接名称，用于标识连接
     */
    private String connName;

    /**
     * 数据库类型，如 MySQL、PostgreSQL 等
     */
    private Long dbTypeId;

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