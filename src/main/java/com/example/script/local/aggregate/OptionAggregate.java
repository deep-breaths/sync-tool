package com.example.script.local.aggregate;

import com.example.script.local.service.SyncConnService;
import com.example.script.local.service.SyncVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author albert lewis
 * @date 2023/12/29
 */
@Component
public class OptionAggregate {
    @Autowired
    private SyncVersionService syncVersionService;
    @Autowired
    private  SyncConnService syncConnService;

}
