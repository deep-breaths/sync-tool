package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据库类型字典
 *
 * @TableName db_type_dict
 */
@TableName(value = "db_type_dict")
@Data
public class DbTypeDict implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

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
    private String prefix;

    /**
     * 后缀，?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai
     */
    private String suffix;

    /**
     * 测试连接的sql语句
     */
    private String test;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}