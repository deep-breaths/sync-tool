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
    private Object sourceDataParam;
    /**\
     * 目标据相关参数
     */
    private Object targetDataParam;
    /**
     * 类型：init,diff
     */
    private String type;
    /**
     * 当isExecute为true时更新targetDataParam数据库
     * type=init执行初始化语句
     * type=diff执行差异化语句
     */
    private Boolean isExecute;

    /**
     * 导入导出规则
     */
    private JSONObject rule;

    private String rulePath;
    /**
     * 输出文件路径
     */
    private String outputPath;

}
