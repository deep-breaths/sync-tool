package com.example.script.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.script.common.domain.CommonModel;
import com.example.script.config.StringListTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 
 * @TableName sync_table
 */
@TableName(value ="sync_table")
@Data
@Accessors(chain = true)
public class SyncTable extends CommonModel<SyncTable> {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 版本id
     */
    private Long versionId;

    /**
     * 数据库id
     */
    private Long dbId;
    private String dbName;
    /**
     * 表名
     *
     */
    private String name;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 表类型，table、view
     */
    private String type;


    /**
     * 建表语句
     */
    private String tableStatement;
    /**
     * 编码等 ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> tableOptions;
}