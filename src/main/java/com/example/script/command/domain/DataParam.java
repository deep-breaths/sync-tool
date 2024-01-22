package com.example.script.command.domain;

import com.alibaba.druid.util.JdbcConstants;
import lombok.Data;

/**
 * @author albert lewis
 * @date 2024/1/22
 */
@Data
public class DataParam {
    /**
     * 数据源类型：ds:数据源，file:sql文件
     */
    private String type;
    /**\
     *数据库类型：mysql
     */
    private String dbType= JdbcConstants.MYSQL.name();
}
