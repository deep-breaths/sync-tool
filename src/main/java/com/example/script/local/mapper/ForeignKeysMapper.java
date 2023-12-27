package com.example.script.local.mapper;

import com.example.script.local.entity.ForeignKeys;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author albert
* @description 针对表【foreign_keys(存储外键信息的表)】的数据库操作Mapper
* @createDate 2023-12-27 20:37:17
* @Entity generator.entity.ForeignKeys
*/
@Mapper
public interface ForeignKeysMapper extends BaseMapper<ForeignKeys> {

}




