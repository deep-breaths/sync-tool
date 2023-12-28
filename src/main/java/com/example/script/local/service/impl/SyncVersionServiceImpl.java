package com.example.script.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.script.local.entity.SyncVersion;
import com.example.script.local.mapper.SyncVersionMapper;
import com.example.script.local.service.SyncVersionService;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【sync_version(数据库版本)】的数据库操作Service实现
* @createDate 2023-12-28 10:27:13
*/
@Service
public class SyncVersionServiceImpl extends ServiceImpl<SyncVersionMapper, SyncVersion>
    implements SyncVersionService{

}




