package com.example.script.local.mapper;

import com.example.script.local.entity.DatabaseVersions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author albert
* @description 针对表【database_versions(数据库版本)】的数据库操作Mapper
* @createDate 2023-12-27 20:37:17
* @Entity generator.entity.DatabaseVersions
*/
@Mapper
public interface DatabaseVersionsMapper extends BaseMapper<DatabaseVersions> {

}




