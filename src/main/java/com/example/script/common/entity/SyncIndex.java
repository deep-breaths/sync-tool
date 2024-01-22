package com.example.script.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.script.common.domain.CommonModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 存储索引信息的表
 *
 * @TableName sync_index
 */
@TableName(value = "sync_index")
@Data
@Accessors(chain = true)
public class SyncIndex extends CommonModel<SyncIndex> {
    /**
     *
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
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

    private String dbName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 索引名称
     */
    private String indexName;


    private String compositeCol;

    /**
     * 是否不是唯一
     */
    private Boolean nonUnique;

    /**
     * 索引类型
     */
    private String type;
}