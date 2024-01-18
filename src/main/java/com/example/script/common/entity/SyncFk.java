package com.example.script.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.script.common.domain.CommonModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 存储外键信息的表
 * @TableName sync_fk
 */
@TableName(value ="sync_fk")
@Data
@Accessors(chain = true)
public class SyncFk extends CommonModel<SyncFk> {
    /**
     * ID
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
    private String tableName;
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
}