package com.example.script.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.script.local.entity.DbTypeDict;
import com.example.script.local.mapper.DbTypeDictMapper;
import com.example.script.local.service.DbTypeDictService;
import org.springframework.stereotype.Service;

/**
* @author albert
* @description 针对表【db_type_dict(数据库类型字典)】的数据库操作Service实现
* @createDate 2023-12-27 20:37:17
*/
@Service
public class DbTypeDictServiceImpl extends ServiceImpl<DbTypeDictMapper, DbTypeDict>
    implements DbTypeDictService {

}




