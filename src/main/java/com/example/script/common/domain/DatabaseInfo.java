package com.example.script.common.domain;

import com.example.script.common.entity.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author albert lewis
 * @date 2023/12/31
 */
@Data
@Accessors(chain = true)
public class DatabaseInfo {
    private List<SyncDb> dbList;
    private List<SyncTable> tableList;
    private List<SyncCol> colList;
    private List<SyncIndex> indexList;
    private List<SyncFk> fkList;
    private List<SyncData> dataList;




}
