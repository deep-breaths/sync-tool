package com.example.script.common.strategy;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
public interface DataSourceStrategy {

    String getName();
    void createDataSource(String url, String username, String password) throws SQLException;

    Map<String, Map<String, List<String>>> toGetInitData() throws SQLException;

    void closeConn() throws SQLException;
}
