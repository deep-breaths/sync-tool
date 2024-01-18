package com.example.script.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.script.common.domain.CommonModel;
import lombok.Data;

/**
 * 数据库版本
 *
 * @TableName sync_version
 */
@TableName(value = "sync_version")
@Data
public class SyncVersion extends CommonModel<SyncVersion> {
    /**
     * 版本ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 外键，SyncConn
     */
    private Long connId;

    /**
     * 版本号
     */
    private String versionNumber;

    /**
     * 版本描述
     */
    private String remarks;

}