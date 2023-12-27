package com.example.script.factory;

import com.example.script.product.strategy.DataBaseStrategy;
import com.example.script.utils.SpringUtils;
import org.springframework.stereotype.Component;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@Component
public class DataBaseFactory implements DataSourceFactory{
    @Override
    public DataBaseStrategy getDataSource(String dbType) {
        String format=getName()+"%s";
        return SpringUtils.getBean(String.format(format, dbType));
    }
    @Override
    public String getName() {

        return "db";
    }
}
