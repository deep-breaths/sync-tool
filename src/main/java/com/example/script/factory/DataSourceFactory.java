package com.example.script.factory;

import com.example.script.product.strategy.DataSourceStrategy;

/**
 * @author albert lewis
 * @date 2023/12/26
 */
public interface DataSourceFactory {
    DataSourceStrategy getDataSource(String dbType);
}
