package com.example.script.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.script.common.entity.SyncDb;
import com.example.script.local.mapper.SyncDbMapper;
import com.example.script.local.service.SyncDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【sync_db(数据库列表)】的数据库操作Service实现
* @createDate 2023-12-28 10:27:13
*/
@Service
public class SyncDbServiceImpl extends ServiceImpl<SyncDbMapper, SyncDb>
    implements SyncDbService{
@Autowired
SyncDbMapper syncDbMapper;


    @Override
    public void insertData(SyncDb syncDb) {
        syncDbMapper.insertData(syncDb);
    }

    @Override
    public void insertData1(SyncDb syncDb) {
        syncDbMapper.insertData1(syncDb);
    }
}




