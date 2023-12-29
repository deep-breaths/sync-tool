package com.example.script.common.factory;

import com.example.script.common.strategy.DataBaseStrategy;
import com.example.script.common.strategy.DataSourceStrategy;
import com.example.script.test.utils.SpringUtils;
import org.springframework.stereotype.Component;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@Component
public class DataBaseFactory implements DataSourceFactory {

    @Override
    public DataSourceStrategy getDataSource(String dbType) {
        if (dbType == null || dbType.isEmpty()) {
            return null;
        }
        String[] subBeanNames = SpringUtils.getSubBeanNames(DataBaseStrategy.class);
        for (String subBeanName : subBeanNames) {
            DataBaseStrategy dataBaseStrategy = SpringUtils.getBean(subBeanName, DataBaseStrategy.class);
            if (dbType.equals(dataBaseStrategy.getName())) {
                return dataBaseStrategy;
            }
        }

        return null;
    }
}
