package com.example.script.test;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author albert lewis
 * @date 2024/1/11
 */

//        BackupRule build = new BackupRule()
//                .buildDB("nacos")
//                .buildDB("ice")
//                .buildDB("dict-center")
//                .build()
//                .buildDB("job-center")
//                .buildTable("app_info")
//                .buildTable("job_info")
//                .build()
//                .buildDB("user-center")
//                .buildTable("sys_dept", some -> some.where("id=1275397643669949952"))
//                .buildTable("sys_role", some -> some.where("id>=1275397643669949952"))
//                .buildTable("sys_role")
//                .build();
//
//        Map<String, DBRule> ruleMap = build.getRuleMap();
//        System.out.println(ruleMap);
//    }
//
//    static class BackupRule {
//        private String currentDB;
//        private DBRule currentDBRule;
//        private TableRule currentTableRule;
//        private Map<String, DBRule> ruleMap;
//
//        public Map<String, DBRule> getRuleMap() {
//            return ruleMap;
//        }
//
//        public BackupRule() {
//
//        }
//
//        public BackupRule(String currentDB, Map<String, DBRule> ruleMap) {
//            this.currentDB = currentDB;
//            this.ruleMap = ruleMap;
//            this.currentDBRule = new DBRule(currentDB);
//            this.currentDBRule.setInclude(true);
//            this.currentDBRule.setTableRule(new LinkedHashMap<>());
//            this.ruleMap.put(currentDB, currentDBRule);
//        }
//
//        public BackupRule buildDB(String dbName) {
//            this.currentDB = dbName;
//            if (this.ruleMap == null) {
//                this.ruleMap = new LinkedHashMap<>();
//            }
//            this.currentDBRule = new DBRule(currentDB);
//            this.currentDBRule.setInclude(true);
//            this.currentDBRule.setTableRule(new LinkedHashMap<>());
//            this.ruleMap.put(currentDB, currentDBRule);
//            return this;
//        }
//
//        public BackupRule buildTable(String tableName) {
//            this.currentTableRule = new TableRule(tableName);
//            this.currentDBRule.getTableRule().put(tableName, currentTableRule);
//            return this;
//        }
//
//        public BackupRule buildTable(String tableName, Consumer<Some> someConsumer) {
//            buildTable(tableName);
//            someConsumer.accept(new Some());
//            return this;
//        }
//
//        public BackupRule build() {
//            return this;
//        }
//
//        public class Some {
//            public void where(String condition) {
//                currentTableRule.setWhere(condition);
//            }
//        }
//    }

public class DatabaseExportRuleMapBuilder {
    public static void main(String[] args) {
        BackupRule build = new BackupRule("nacos")
                .buildDB("ice")
                .buildDB("dict-center")
                .build()
                .buildDB("job-center",table->table.name("app_info").name("job_info"))
                .build()
                .buildDB("user-center",table->table.name("sys_dept",some -> some.where("id=1275397643669949952"))
                                                   .name("sys_dept",some -> some.where("id=1275397643669949952"))
                                                   .name("sys_role")
                )

                .build();

        Map<String, DBRule> ruleMap = build.getRuleMap();
        System.out.println(ruleMap);
    }

    static class BackupRule {
        private String currentDB;
        private DBRule currentDBRule;
        private TableRule currentTableRule;
        private Map<String, DBRule> ruleMap;

        public Map<String, DBRule> getRuleMap() {
            return ruleMap;
        }

        public BackupRule(String currentDB) {
            this.currentDB = currentDB;
            this.ruleMap = new LinkedHashMap<>();
            this.currentDBRule = new DBRule(currentDB);
            this.currentDBRule.setIsInclude(true);
            this.currentDBRule.setTableRule(new LinkedHashMap<>());
            this.ruleMap.put(currentDB, currentDBRule);
        }

        public BackupRule buildDB(String dbName) {
            this.currentDB = dbName;
            this.currentDBRule = new DBRule(currentDB);
            this.currentDBRule.setIsInclude(true);
            this.currentDBRule.setTableRule(new LinkedHashMap<>());
            this.ruleMap.put(currentDB, currentDBRule);
            return this;
        }

        public BackupRule buildDB(String dbName, Consumer<BackupRule> tableConsumer) {
            buildDB(dbName);
            tableConsumer.accept(this);
            return this;
        }

        public BackupRule name(String tableName) {
            this.currentTableRule = new TableRule(tableName);
            this.currentDBRule.getTableRule().put(tableName, currentTableRule);
            return this;
        }

        public BackupRule name(String tableName, Consumer<Some> someConsumer) {
            name(tableName);
            someConsumer.accept(new Some());
            return this;
        }

        public BackupRule build() {
            return this;
        }

        public class Some {
            public void where(String condition) {
                currentTableRule.setWhere(condition);
            }
        }
    }



    @Data
    static class DBRule {

        private String name;
        private Boolean isInclude = false;

        private Boolean includeData = false;
        private Boolean isAllData = false;

        private Map<String, TableRule> tableRule;
        private Boolean isMultiTenant = false;

        public DBRule(String name) {
            this.name = name;
        }
    }

    @Data
    static class TableRule {
        private String name;
        private String where;
        private List<String> update;
        private Boolean isAll = false;
        private Boolean isMultiTenant = false;

        public TableRule(String name) {
            this.name = name;
        }
    }
}