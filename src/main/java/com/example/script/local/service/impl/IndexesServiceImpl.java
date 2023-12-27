package com.example.script.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.script.local.entity.Indexes;
import com.example.script.local.mapper.IndexesMapper;
import com.example.script.local.service.IndexesService;
import org.springframework.stereotype.Service;

/**
* @author albert
* @description 针对表【indexes(存储索引信息的表)】的数据库操作Service实现
* @createDate 2023-12-27 20:37:17
*/
@Service
public class IndexesServiceImpl extends ServiceImpl<IndexesMapper, Indexes>
    implements IndexesService {

}




