package com.example.script.factory;

import com.example.script.product.strategy.DataBaseStrategy;

/**
 * @author albert lewis
 * @date 2023/12/26
 */
public interface DataSourceFactory {
    DataBaseStrategy getDataSource(String dbType);
    String getName();
}
