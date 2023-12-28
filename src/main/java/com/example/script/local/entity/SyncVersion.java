package com.example.script.local.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据库版本
 * @TableName sync_version
 */
@TableName(value ="sync_version")
@Data
public class SyncVersion implements Serializable {
    /**
     * 版本ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 外键，DatabaseConnections
     */
    private Long connectionId;

    /**
     * 版本号
     */
    private String versionNumber;

    /**
     * 版本描述
     */
        private String remarks;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

}