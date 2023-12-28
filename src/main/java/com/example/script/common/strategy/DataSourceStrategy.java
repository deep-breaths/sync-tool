package com.example.script.common.strategy;

import java.sql.SQLException;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
public interface DataSourceStrategy {

    String getName();
    void createDataSource(String url, String username, String password) throws SQLException;

    void closeConn() throws SQLException;
}
