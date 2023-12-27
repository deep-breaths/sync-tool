package com.example.script.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.script.local.entity.ForeignKeys;
import com.example.script.local.mapper.ForeignKeysMapper;
import com.example.script.local.service.ForeignKeysService;
import org.springframework.stereotype.Service;

/**
* @author albert
* @description 针对表【foreign_keys(存储外键信息的表)】的数据库操作Service实现
* @createDate 2023-12-27 20:37:17
*/
@Service
public class ForeignKeysServiceImpl extends ServiceImpl<ForeignKeysMapper, ForeignKeys>
    implements ForeignKeysService {

}




