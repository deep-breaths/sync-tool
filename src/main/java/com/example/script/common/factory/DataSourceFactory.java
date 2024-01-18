package com.example.script.common.factory;

import com.example.script.common.strategy.DataSourceStrategy;
import com.example.script.common.utils.SpringUtils;

/**
 * @author albert lewis
 * @date 2023/12/27
 */

public class DataSourceFactory {

    public static DataSourceStrategy getDataSource(String dbType) {
        if (dbType == null || dbType.isEmpty()) {
            return null;
        }
        String[] subBeanNames = SpringUtils.getSubBeanNames(DataSourceStrategy.class);
        for (String subBeanName : subBeanNames) {
            DataSourceStrategy dataBaseStrategy = SpringUtils.getBean(subBeanName, DataSourceStrategy.class);
            if (dbType.equals(dataBaseStrategy.getName())) {
                return dataBaseStrategy;
            }
        }

        return null;
    }
}
