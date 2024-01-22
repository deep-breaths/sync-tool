package com.example.script.command.domain;

import lombok.Data;

/**
 * @author albert lewis
 * @date 2024/1/22
 */
@Data
public class DataSourceParam extends DataParam {

    /**
     * 链接
     */
    private String url;
    /**
     * 账号
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 驱动
     */
    private String driverName;
}
