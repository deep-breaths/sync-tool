package com.example.script.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.script.common.domain.CommonModel;
import lombok.Data;

/**
 * 数据库类型字典
 * @TableName sync_db_type
 */
@TableName(value ="sync_db_type")
@Data
public class SyncDbType extends CommonModel<SyncDbType> {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 数据库类型，如mysql
     */
    private String type;

    /**
     * 驱动名称，如com.mysql.cj.jdbc.Driver
     */
    private String driverName;

    /**
     * 前缀，jdbc:mysql://
     */
    @TableField(value = "prefix")
    private String prefix;

    /**
     * 后缀，?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai
     */
    private String suffix;

    /**
     * 建库语句
     */
    private String structure;

    /**
     * 测试连接的sql语句
     */
    private String test;
}