package com.example.script;

import com.alibaba.druid.util.JdbcConstants;
import com.example.script.constant.DataSourceTypeEnum;
import com.example.script.factory.DataSourceFactory;
import com.example.script.product.strategy.DataSourceStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@SpringBootTest
public class DataSourceFactoryTest {

    @Test
    void context(){
        DataSourceFactory dataSourceFactory = DataSourceTypeEnum.toGetFactory(0);
        DataSourceStrategy dataSource = dataSourceFactory.getDataSource(JdbcConstants.MYSQL.name());
        System.out.println(dataSource);
        DataSourceFactory dataSourceFactory1 = DataSourceTypeEnum.toGetFactory(1);
        DataSourceStrategy dataSource1 = dataSourceFactory1.getDataSource(JdbcConstants.MYSQL.name());
        System.out.println(dataSource1);
    }
}
