package com.example.script.local.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.script.local.entity.DatabaseConnections;
import org.apache.ibatis.annotations.Mapper;

/**
* @author albert
* @description 针对表【database_connections(数据库连接信息)】的数据库操作Mapper
* @createDate 2023-12-27 20:37:17
* @Entity generator.entity.DatabaseConnections
*/
@Mapper
public interface DatabaseConnectionsMapper extends BaseMapper<DatabaseConnections> {

}




