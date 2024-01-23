package com.example.script.common.rule;

import com.example.script.common.rule.condition.DBParam;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author albert lewis
 * @date 2024/1/12
 */
public class RuleUtils {
    @Getter
    @Setter
    private static Map<String, DBRule> ruleMap;

    private static final String IGNORE = "undo_log";


    static {
        //默认导出指定数据库的所有表结构（不包含数据），规则中存在的表则表示要导出数据，具体的数据范围单独配置
        BackupRule build = new BackupRule()
                .buildDB("seata-server")
                .buildDB("dimmer-center")
                .buildDB("log-center")
                .buildDB("nacos", DBParam::isAllData)
                .buildDB("ice", DBParam::isAllData)
                .buildDB("dict-center", DBParam::isAllData)
                .buildDB("job-center", DBParam::notIsAllData, table -> table.name("app_info").name(
                        "job_info"))
                .buildDB("user-center", dbParam -> dbParam.notIsAllData().isMultiTenant(), table -> table
                        .name("sys_dept",
                              some -> some.where("dept_id=1"))
                        .name("sys_role", some -> some.where("id in (1,2,3)"))
                        .name("sys_role_menu", some -> some.where("role_id in (1,2,3)"))
                        .name("sys_role_user", some -> some.where("user_id=1275397643669949952"))
                        .name("sys_user", some -> some.where("id=1275397643669949952"))
                )
                .buildDB("biz-center", dbParam -> dbParam.notIsAllData().isMultiTenant(), table -> table
                        .name("meeting_display_board_template")
                        .name("meeting_type"))
                .buildDB("file-center", DBParam::notIsAllData, table -> table
                        .name("file_info", some -> some.where("bucket_name='sys-file-resource'"))
                        .name("system_material_file_info"))
                .buildDB("oauth-center", DBParam::isAllData)
                .buildDB("device-center", DBParam::notIsAllData, table -> table
                        .name("dev_product")
                        .name("dev_product_tag")
                        .name("dev_tag")
                        .name("mqtt_acl")
                        .name("mqtt_custom_topic")
                        .name("mqtt_user", some -> some.where("limit 0,3"))
                        .name("protocol_service_classify")
                        .name("protocol_service_type")
                )
                .buildDB("visual_face_search", dbParam -> dbParam.notIsAllStruct().notIncludeData(), table -> table
                        .name("visual_collection"))

                .build();

        ruleMap = build.getRuleMap();
    }

    public static Boolean checkIsExportDB(String databaseName) {
        DBRule dbRule = ruleMap.get(databaseName);
        /**
         * 默认是存在则导出
         */
        if (dbRule == null) {
            return Boolean.FALSE;
        }
        if (!dbRule.getIsInclude()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;

    }

    public static Boolean checkIsExportTableStruct(String databaseName, String tableName) {
        DBRule dbRule = ruleMap.get(databaseName);
        if (dbRule == null) {
            return Boolean.FALSE;
        }
        if (dbRule.getIsAllStruct()) {
            return Boolean.TRUE;
        }


        if (tableName.equalsIgnoreCase(IGNORE)) {
            return Boolean.TRUE;
        }
        Map<String, TableRule> tableRule = dbRule.getTableRule();
        TableRule currentTableRule = tableRule.get(tableName);
        if (currentTableRule == null) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;

    }

    public static Boolean checkThisDbIsExportData(String databaseName) {
        DBRule dbRule = ruleMap.get(databaseName);
        if (dbRule == null) {
            return Boolean.FALSE;
        }
        if (!dbRule.getIncludeData()) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    public static ExportDataRule getTableDataCondition(String databaseName, String tableName) {
        DBRule dbRule = ruleMap.get(databaseName);
        ExportDataRule exportDataRule = new ExportDataRule();
        if (tableName.equalsIgnoreCase(IGNORE)) {
            exportDataRule.setIncludeData(Boolean.FALSE);
            return exportDataRule;
        }
        if (dbRule.getIsAllData()) {
            exportDataRule.setIncludeData(Boolean.TRUE);
            Map<String, TableRule> tableRule = dbRule.getTableRule();
            TableRule currentTableRule = tableRule.get(tableName);
            if (currentTableRule != null) {
                exportDataRule.setUpdate(currentTableRule.getUpdate());
            }
            return exportDataRule;
        }


        Map<String, TableRule> tableRule = dbRule.getTableRule();
        TableRule currentTableRule = tableRule.get(tableName);
        if (currentTableRule == null) {
            exportDataRule.setIncludeData(Boolean.FALSE);
            return exportDataRule;
        }
        if (!currentTableRule.getIncludeData()) {
            exportDataRule.setIncludeData(Boolean.FALSE);
            exportDataRule.setUpdate(currentTableRule.getUpdate());
            return exportDataRule;
        }
        if (currentTableRule.getIsAllData()) {
            exportDataRule.setIncludeData(Boolean.TRUE);
            exportDataRule.setUpdate(currentTableRule.getUpdate());
            return exportDataRule;
        }

        exportDataRule.setIncludeData(Boolean.TRUE);
        exportDataRule.setWhere(currentTableRule.getWhere());
        exportDataRule.setUpdate(currentTableRule.getUpdate());
        return exportDataRule;

    }

    public static String toSetWhere(String where, String sql) {
        if (where != null && !where.isBlank()) {
            if (where.startsWith("limit")) {
                sql = String.format("%s %s", sql, where);
            } else {
                sql = String.format("%s WHERE %s", sql, where);
            }
        }
        return sql;
    }

    public static Boolean isMultiTenantDB(String databaseName) {
        DBRule dbRule = ruleMap.get(databaseName);
        if (dbRule.getIsMultiTenant() == null || !dbRule.getIsMultiTenant()) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
    public static Boolean isMultiTenantData(String databaseName,String tableName) {
        DBRule dbRule = ruleMap.get(databaseName);
        if (dbRule==null){
            return Boolean.FALSE;
        }
        if (dbRule.getIsMultiTenant() == null || !dbRule.getIsMultiTenant()) {
            return Boolean.FALSE;
        }
        Map<String, TableRule> tableRule = dbRule.getTableRule();
        TableRule currentTableRule = tableRule.get(tableName);
        if (currentTableRule==null){
            return Boolean.FALSE;
        }
        if (!currentTableRule.getIsMultiTenant()){
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}
