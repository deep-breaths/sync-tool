package com.example.script.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.script.local.entity.DatabaseStructures;
import com.example.script.local.mapper.DatabaseStructuresMapper;
import com.example.script.local.service.DatabaseStructuresService;
import org.springframework.stereotype.Service;

/**
* @author albert
* @description 针对表【database_structures(数据库列表)】的数据库操作Service实现
* @createDate 2023-12-27 20:37:17
*/
@Service
public class DatabaseStructuresServiceImpl extends ServiceImpl<DatabaseStructuresMapper, DatabaseStructures>
    implements DatabaseStructuresService {

}




