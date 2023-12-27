package com.example.script.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.script.local.entity.DatabaseConnections;
import com.example.script.local.mapper.DatabaseConnectionsMapper;
import com.example.script.local.service.DatabaseConnectionsService;
import org.springframework.stereotype.Service;

/**
* @author albert
* @description 针对表【database_connections(数据库连接信息)】的数据库操作Service实现
* @createDate 2023-12-27 20:37:17
*/
@Service
public class DatabaseConnectionsServiceImpl extends ServiceImpl<DatabaseConnectionsMapper, DatabaseConnections>
    implements DatabaseConnectionsService {

}




