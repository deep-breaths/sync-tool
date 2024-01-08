package com.example.script.common.rule;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author albert lewis
 * @date 2024/1/3
 */
@Data
public class BackupRule {

    private List<String> exclude=List.of("mysql","performance_schema","sys","information_schema","standalone","*-biz" +
            "-center","-user-center");
    private List<String> include=List.of("visual_face_search.visual_collection");

    private Boolean isInclude=true;

    private Map<String,Map<String,TableRule>>  dataRules;

    private Boolean isAllData=false;

}
