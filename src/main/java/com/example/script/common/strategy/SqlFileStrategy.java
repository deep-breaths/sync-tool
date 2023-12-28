package com.example.script.common.strategy;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
public abstract class SqlFileStrategy implements DataSourceStrategy {
    private String name;

    public abstract String getName();

    @Override
    public void createDataSource(String url, String username, String password) {

    }
    @Override
    public void closeConn(){

    }

}
