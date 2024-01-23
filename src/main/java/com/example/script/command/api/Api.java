package com.example.script.command.api;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.example.script.command.domain.DataSourceParam;
import com.example.script.command.domain.Param;
import com.example.script.command.domain.SqlFileParam;
import com.example.script.common.rule.DBRule;
import com.example.script.common.rule.RuleUtils;
import com.example.script.constant.SQLSaveType;
import com.example.script.domain.DiffDDL;
import com.example.script.utils.DBUtils;
import com.example.script.utils.FileUtils;
import com.example.script.utils.MigrationUtils;
import com.example.script.utils.comparator.sqlfile.DataFileComparator;
import com.example.script.utils.comparator.sqlfile.TableFileComparator;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author albert lewis
 * @date 2024/1/22
 */
public class Api {

    public void execute(String command){
        Param param = JSONUtil.toBean(command, Param.class);
        execute(param);

    }

    public void execute(Param param) {
        String outputPath = param.getOutputPath();
        if (outputPath==null||outputPath.isBlank()){
            outputPath=".";
        }
        if (param.getIsExecute()!=null&& param.getIsExecute()&&("init".equalsIgnoreCase(param.getType())||"diff".equalsIgnoreCase(param.getType()))){
            toExecuteSQL(param, outputPath);
            return;
        }
        if ("init".equalsIgnoreCase(param.getType())){
            getInitSQL(param, outputPath);

        }else if ("diff".equalsIgnoreCase(param.getType())){
            getDiffSQL(param, outputPath);
        }
    }

    private void toExecuteSQL(Param param, String outputPath) {
        String targetDataParam = param.getTargetDataParam();
        if (targetDataParam==null||targetDataParam.isBlank()){
            throw new RuntimeException("数据源参数不能为空");
        }

        DataSourceParam dataSourceParam = JSONUtil.toBean(targetDataParam, DataSourceParam.class);
        try (DruidDataSource dataSource = DBUtils.createDataSource(dataSourceParam.getUrl(), dataSourceParam.getUserName(),
                                                                         dataSourceParam.getPassword());) {




            String message = """
                    **************************
                    **********%s***********
                    **************************""";
            System.err.printf((message) + "%n", "更新数据库开始");
            MigrationUtils.toExecuteSQL(dataSource,outputPath,param.getType());
            System.err.printf((message) + "%n", "更新数据库结束");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getInitSQL(Param param, String outputPath) {
        JSONObject rule = param.getRule();
        if (rule!=null){
            Map<String, DBRule> ruleMap = JSONUtil.toBean(JSONUtil.parse(rule), new TypeReference<>() {
            }, true);
            RuleUtils.setRuleMap(ruleMap);
        }
        String sourceDataParam = param.getSourceDataParam();
        if (sourceDataParam==null||sourceDataParam.isBlank()){
            throw new RuntimeException("数据源参数不能为空");
        }

        getInitSQL(outputPath, sourceDataParam);
    }

    private void getDiffSQL(Param param, String outputPath) {
        JSONObject rule = param.getRule();
        if (rule!=null){
            Map<String, DBRule> ruleMap = JSONUtil.toBean(JSONUtil.parse(rule), new TypeReference<>() {
            }, true);
            RuleUtils.setRuleMap(ruleMap);
        }
        checkParam(param);
        String sourceType = param.getSourceType();
        String targetType = param.getTargetType();
        String sourceDataParam = param.getSourceDataParam();
        String targetDataParam = param.getTargetDataParam();
        String sourceFilePath = "";
        String targetFilePath = "";
        if (sourceType.equalsIgnoreCase("ds")){
            FileUtils.deleteFile("./tmp");
            sourceFilePath = "./tmp/source";
            getInitSQL(sourceFilePath, sourceDataParam);
        }else if (sourceType.equalsIgnoreCase("file")){
            SqlFileParam sqlFileParam = JSONUtil.toBean(sourceDataParam, SqlFileParam.class);
            sourceFilePath=sqlFileParam.getFilePath();
        }

        if (targetType.equalsIgnoreCase("ds")){
            targetFilePath="./tmp/target";
            getInitSQL(targetFilePath,targetDataParam);
        }else if (targetType.equalsIgnoreCase("file")){
            SqlFileParam sqlFileParam = JSONUtil.toBean(targetDataParam, SqlFileParam.class);
            targetFilePath=sqlFileParam.getFilePath();
        }
        String message = """
                **************************
                **********%s***********
                **************************""";
        System.err.printf((message) + "%n", "获取初始化数据");
        Map<String, Map<String, List<String>>> sourceInitSQL = MigrationUtils.getInitSQL(sourceFilePath);
        Map<String, Map<String, List<String>>> targetInitSQL = MigrationUtils.getInitSQL(targetFilePath);
        System.err.printf((message) + "%n", "差异化DDL开始");
        DiffDDL diffDDL = TableFileComparator.getDiffDDL(sourceInitSQL.get(SQLSaveType.DDL_CREATE), targetInitSQL.get(SQLSaveType.DDL_CREATE));
        Map<String, Map<String, List<String>>> diffSchemas = diffDDL.getDiffSchemas();
        FileUtils.process(diffSchemas, FileUtils::saveToFile, outputPath, "diff");
        System.err.printf((message) + "%n", "差异化DDL开始");
        System.err.printf((message) + "%n", "差异化DML开始");
        Map<String, Map<String, Map<String, Set<String>>>> targetAllTableKeys = TableFileComparator.getAllPrimaryOrUniqueKeys(targetInitSQL.get(SQLSaveType.DDL_CREATE));
        Map<String, Map<String, List<String>>> diffDML = DataFileComparator.getDiffDML(sourceInitSQL,targetInitSQL,
                                                                                       diffDDL.getSourceKeys(),
                                                                                       targetAllTableKeys);
        FileUtils.process(diffDML, FileUtils::saveToFile, outputPath, "diff");
        System.err.printf((message) + "%n", "差异化DML结束");
    }

    private void checkParam(Param param) {
        if (param.getSourceType()==null){
            throw new RuntimeException("源数据类型不能为空");
        }
        if (!param.getSourceType().equalsIgnoreCase("ds") &&!param.getSourceType().equalsIgnoreCase("file")){
            throw new RuntimeException("源数据类型不正确（ds:数据源，file:sql文件）");
        }
        if (param.getSourceDataParam()==null||param.getSourceDataParam().isBlank()){
            throw new RuntimeException("源数据参数不能为空");
        }
        if (param.getTargetType()==null){
            throw new RuntimeException("目标数据类型不能为空");
        }
        if (!param.getTargetType().equalsIgnoreCase("ds") &&!param.getTargetType().equalsIgnoreCase("file")){
            throw new RuntimeException("目标数据类型不正确（ds:数据源，file:sql文件）");
        }
        if (param.getTargetDataParam()==null||param.getTargetDataParam().isBlank()){
            throw new RuntimeException("源数据参数不能为空");
        }
    }

    private void getInitSQL(String outputPath,String sourceDataParam){
        DataSourceParam dataSourceParam = JSONUtil.toBean(sourceDataParam, DataSourceParam.class);
        try (DruidDataSource sourceDataSource = DBUtils.createDataSource(dataSourceParam.getUrl(), dataSourceParam.getUserName(),
                                                                         dataSourceParam.getPassword());) {

            Connection sourceConn = sourceDataSource.getConnection();


            String message = """
                    **************************
                    **********%s***********
                    **************************""";
            System.err.printf((message) + "%n", "初始化SQL开始");
            Map<String, Map<String, List<String>>> initSQL = MigrationUtils.getInitSQL(sourceConn);
            FileUtils.process(initSQL, FileUtils::saveToFile, outputPath, "init");
            System.err.printf((message) + "%n", "初始化SQL结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
