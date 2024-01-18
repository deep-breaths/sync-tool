package com.example.script.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.script.common.domain.CommonModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 数据库列表
 * @TableName sync_db
 */
@TableName(value ="sync_db")
@Data
@Accessors(chain = true)
public class SyncDb extends CommonModel<SyncDb> {
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
     * 数据库名
     */
    private String schemaName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}