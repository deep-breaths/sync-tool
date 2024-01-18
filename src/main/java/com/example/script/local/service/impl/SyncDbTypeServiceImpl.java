package com.example.script.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.script.common.entity.SyncDbType;
import com.example.script.local.mapper.SyncDbTypeMapper;
import com.example.script.local.service.SyncDbTypeService;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【sync_db_type(数据库类型字典)】的数据库操作Service实现
* @createDate 2023-12-28 10:27:13
*/
@Service
public class SyncDbTypeServiceImpl extends ServiceImpl<SyncDbTypeMapper, SyncDbType>
    implements SyncDbTypeService{

}




