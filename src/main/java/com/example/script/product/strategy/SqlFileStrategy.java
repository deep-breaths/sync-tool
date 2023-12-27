package com.example.script.product.strategy;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
public abstract class SqlFileStrategy implements DataSourceStrategy {
    private String name;

    public abstract String getName();

}
