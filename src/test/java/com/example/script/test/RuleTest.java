package com.example.script.test;

import com.example.script.common.rule.BackupRule;
import com.example.script.common.rule.DBRule;
import com.example.script.common.rule.condition.DBParam;

import java.util.Map;

/**
 * @author albert lewis
 * @date 2024/1/12
 */
public class RuleTest {

    public static void main(String[] args) {
        //默认导出指定数据库的所有表结构（不包含数据），规则中存在的表则表示要导出数据，具体的数据范围单独配置
        BackupRule build = new BackupRule()
                .buildDB("nacos", DBParam::isAllData)
                .buildDB("ice", DBParam::isAllData)
                .buildDB("dict-center", DBParam::isAllData)
                .buildDB("job-center", DBParam::isAllData, table -> table.name("app_info").name("job_info"))
                .buildDB("user-center", dbParam -> dbParam.notIsAllData().isMultiTenant(), table -> table
                        .name("sys_dept",
                              some -> some.where("id=1275397643669949952"))
                        .name("sys_role", some -> some.where("id=1275397643669949952"))
                        .name("sys_role_menu", some -> some.where("id=1275397643669949952"))
                        .name("sys_user", some -> some.where("id=1275397643669949952"))
                )
                .buildDB("biz-center", dbParam -> dbParam.notIsAllData().isMultiTenant(), table -> table
                        .name("meeting_display_board_template")
                        .name("meeting_type"))
                .buildDB("file-center", DBParam::notIsAllData, table -> table
                        .name("file_info", some -> some.where("bucket_name=sys-file-resource"))
                        .name("system_material_file_info"))
                .buildDB("oauth-center", DBParam::isAllData)
                .buildDB("device-center", DBParam::notIsAllData, table -> table
                        .name("dev_product")
                        .name("dev_product_tag")
                        .name("dev_tag")
                        .name("mqtt_acl")
                        .name("mqtt_custom_topic")
                        .name("mqtt_user",some -> some.where("limit 0,3"))
                        .name("protocol_service_classify")
                        .name("protocol_service_type")
                )

                .build();

        Map<String, DBRule> ruleMap = build.getRuleMap();
        System.out.println(ruleMap);
    }
}
