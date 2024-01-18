package com.example.script.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.script.common.entity.SyncCol;
import com.example.script.local.mapper.SyncColMapper;
import com.example.script.local.service.SyncColService;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【sync_col(表字段信息(来源于建表语句的拆解))】的数据库操作Service实现
* @createDate 2023-12-28 10:27:12
*/
@Service
public class SyncColServiceImpl extends ServiceImpl<SyncColMapper, SyncCol>
    implements SyncColService{

}




