package com.example.script;

import com.alibaba.druid.util.JdbcConstants;
import com.example.script.constant.DataSourceTypeEnum;
import com.example.script.factory.DataSourceFactory;
import com.example.script.local.entity.SyncCol;
import com.example.script.local.service.SyncColService;
import com.example.script.product.strategy.DataSourceStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

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
