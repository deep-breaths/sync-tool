package com.example.script.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.script.common.entity.SyncIndex;
import com.example.script.local.mapper.SyncIndexMapper;
import com.example.script.local.service.SyncIndexService;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【sync_index(存储索引信息的表)】的数据库操作Service实现
* @createDate 2023-12-28 10:27:13
*/
@Service
public class SyncIndexServiceImpl extends ServiceImpl<SyncIndexMapper, SyncIndex>
    implements SyncIndexService{

}




