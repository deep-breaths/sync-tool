package com.example.script.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.script.local.entity.DatabaseVersions;
import com.example.script.local.mapper.DatabaseVersionsMapper;
import com.example.script.local.service.DatabaseVersionsService;
import org.springframework.stereotype.Service;

/**
* @author albert
* @description 针对表【database_versions(数据库版本)】的数据库操作Service实现
* @createDate 2023-12-27 20:37:17
*/
@Service
public class DatabaseVersionsServiceImpl extends ServiceImpl<DatabaseVersionsMapper, DatabaseVersions>
    implements DatabaseVersionsService {

}




