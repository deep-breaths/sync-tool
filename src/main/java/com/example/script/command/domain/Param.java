package com.example.script.command.domain;

import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * @author albert lewis
 * @date 2024/1/22
 */
@Data
public class Param {
    /**
     * 源数据类型
     */
    private String sourceType;

    /**
     * 目标数据类型
     */
    private String targetType;

    /**
     * 源数据相关参数
     */
    private String sourceDataParam;
    /**\
     * 目标据相关参数
     */
    private String targetDataParam;
    /**
     * 类型：init,diff
     */
    private String type;

    /**
     * table,data
     */
    private String include;

    /**
     * 导入导出规则
     */
    private JSONObject rule;
    /**
     * 输出文件路径
     */
    private String outputPath;

}
