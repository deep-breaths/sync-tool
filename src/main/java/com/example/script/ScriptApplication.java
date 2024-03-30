package com.example.script;

import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.example.script.command.api.Api;
import com.example.script.command.domain.DataSourceParam;
import com.example.script.command.domain.Param;
import com.example.script.utils.FileUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.script.constant.DBConstant.*;

public class ScriptApplication {
    public static void main(String[] args) {
        StopWatch stopWatch=new StopWatch();
        stopWatch.start("任务");
        Map<String, String> commandParam = getCommandParam(args);
        if (commandParam.get("file")==null||commandParam.get("file").isBlank()){
            throw new RuntimeException("文件路径不存在");
        }

        String paramFilePath = commandParam.get("file");
        String jsonFile = FileUtils.getJsonFile(paramFilePath);
        if (jsonFile==null||jsonFile.isBlank()||!JSONUtil.isTypeJSON(jsonFile)){
            throw new RuntimeException("文件内容格式不正确");
        }

        Api api=new Api();
        api.execute(jsonFile);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }

    private static Map<String,String> getCommandParam(String[] args){
        Map<String, String> commandLineArgs = new HashMap<>();

        for (String arg : args) {
            System.out.println(arg);
            if (arg.startsWith("--")) {
                String[] parts = arg.substring(2).split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    if ("null".equalsIgnoreCase(value)){
                        value=null;
                    }
                    commandLineArgs.put(key, value);
                }
            }
        }
        return commandLineArgs;
    }
}
