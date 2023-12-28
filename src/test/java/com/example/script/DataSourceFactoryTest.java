package com.example.script;

import com.alibaba.druid.util.JdbcConstants;
import com.example.script.constant.DataSourceTypeEnum;
import com.example.script.common.factory.DataSourceFactory;
import com.example.script.local.entity.SyncCol;
import com.example.script.local.service.SyncColService;
import com.example.script.common.strategy.DataSourceStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.script.constant.DBConstant.*;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@SpringBootTest
public class DataSourceFactoryTest {

    @Test
    void context() throws SQLException {
        DataSourceFactory dataSourceFactory = DataSourceTypeEnum.toGetFactory(0);
        DataSourceStrategy dataSourceStrategy = dataSourceFactory.getDataSource(JdbcConstants.MYSQL.name());
        dataSourceStrategy.createDataSource(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);
        DataSourceStrategy dataSourceStrategy2 = dataSourceFactory.getDataSource(JdbcConstants.MYSQL.name());
        dataSourceStrategy2.createDataSource(TARGET_URL, TARGET_USERNAME, TARGET_PASSWORD);
        dataSourceStrategy.closeConn();
        dataSourceStrategy2.closeConn();
        DataSourceFactory dataSourceFactory1 = DataSourceTypeEnum.toGetFactory(1);
        DataSourceStrategy dataSourceStrategy1 = dataSourceFactory1.getDataSource(JdbcConstants.MYSQL.name());
        System.out.println(dataSourceStrategy1);
    }

    @Autowired
    private SyncColService syncColService;
    @Test
    void testSelect(){
        List<SyncCol> columnStructuresList=new ArrayList<>();
        for (int i=0;i<10;i++){
            SyncCol build = SyncCol.builder()
                                                     .defaultValue("23" + i).size(4112L+i).build();
            columnStructuresList.add(build);
        }
        syncColService.saveBatch(columnStructuresList);
        List<SyncCol> list = syncColService.list();

        list.forEach(System.out::println);
    }

}
