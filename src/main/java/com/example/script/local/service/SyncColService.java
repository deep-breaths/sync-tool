package com.example.script.local.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.script.common.entity.SyncCol;

import java.util.List;

/**
* @author admin
* @description 针对表【sync_col(表字段信息(来源于建表语句的拆解))】的数据库操作Service
* @createDate 2023-12-28 10:27:13
*/
public interface SyncColService extends IService<SyncCol> {

    List<SyncCol> selectWithEmpty();
}
