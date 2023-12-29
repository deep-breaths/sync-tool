package com.example.script.common.factory;

import com.example.script.common.strategy.DataSourceStrategy;
import com.example.script.common.strategy.SqlFileStrategy;
import com.example.script.test.utils.SpringUtils;
import org.springframework.stereotype.Component;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@Component
public class SqlFileFactory implements DataSourceFactory {
    @Override
    public DataSourceStrategy getDataSource(String dbType) {
        if (dbType == null || dbType.isEmpty()) {
            return null;
        }
        String[] subBeanNames = SpringUtils.getSubBeanNames(SqlFileStrategy.class);
        for (String subBeanName : subBeanNames) {
            SqlFileStrategy sqlFileStrategy = SpringUtils.getBean(subBeanName, SqlFileStrategy.class);
            if (dbType.equals(sqlFileStrategy.getName())) {
                return sqlFileStrategy;
            }
        }

        return null;
    }
}