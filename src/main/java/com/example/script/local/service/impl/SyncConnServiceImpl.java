package com.example.script.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.script.common.entity.SyncConn;
import com.example.script.local.mapper.SyncConnMapper;
import com.example.script.local.service.SyncConnService;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【sync_conn(数据库连接信息)】的数据库操作Service实现
* @createDate 2023-12-28 10:27:13
*/
@Service
public class SyncConnServiceImpl extends ServiceImpl<SyncConnMapper, SyncConn>
    implements SyncConnService{

}




