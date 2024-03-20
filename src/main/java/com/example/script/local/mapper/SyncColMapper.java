package com.example.script.local.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.script.common.entity.SyncCol;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author admin
* @description 针对表【sync_col(表字段信息(来源于建表语句的拆解))】的数据库操作Mapper
* @createDate 2023-12-28 10:27:12
* @Entity generator.entity.SyncCol
*/
@Mapper
public interface SyncColMapper extends BaseMapper<SyncCol> {

    List<SyncCol> selectWithEmpty();
}




