package com.example.script.common.factory;

import com.example.script.common.strategy.DataSourceStrategy;

/**
 * @author albert lewis
 * @date 2023/12/26
 */
public interface DataSourceFactory {
    DataSourceStrategy getDataSource(String dbType);
}
