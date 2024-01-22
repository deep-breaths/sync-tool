package test;

import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONUtil;
import com.example.script.command.api.Api;
import com.example.script.command.domain.DataSourceParam;
import com.example.script.command.domain.Param;

import java.util.concurrent.TimeUnit;

import static com.example.script.constant.DBConstant.*;

/**
 * @author albert lewis
 * @date 2024/1/22
 */
public class ApiTest {

    private static final Api api=new Api();
    public static void main(String[] args) {
        StopWatch stopWatch=new StopWatch();
        stopWatch.start("任务");
        Param param=new Param();
        diffSQl(param);
        api.execute(JSONUtil.toJsonStr(param));
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }

    private static void diffSQl(Param param) {

        param.setType("diff");
        param.setSourceType("ds");
        DataSourceParam sourceDataSourceParam=new DataSourceParam();
        sourceDataSourceParam.setType(param.getSourceType());
        sourceDataSourceParam.setUrl(SOURCE_URL);
        sourceDataSourceParam.setUserName(SOURCE_USERNAME);
        sourceDataSourceParam.setPassword(SOURCE_PASSWORD);
        sourceDataSourceParam.setDriverName(DRIVER_CLASS_NAME);
        param.setSourceDataParam(JSONUtil.toJsonStr(sourceDataSourceParam));

        param.setTargetType("ds");
        DataSourceParam targetDataSourceParam=new DataSourceParam();
        targetDataSourceParam.setType(param.getSourceType());
        targetDataSourceParam.setUrl(TARGET_URL);
        targetDataSourceParam.setUserName(TARGET_USERNAME);
        targetDataSourceParam.setPassword(TARGET_PASSWORD);
        targetDataSourceParam.setDriverName(DRIVER_CLASS_NAME);
        param.setTargetDataParam(JSONUtil.toJsonStr(targetDataSourceParam));

    }

    private static void initSQl(Param param) {
        param.setType("init");
        param.setSourceType("ds");
        DataSourceParam dataSourceParam=new DataSourceParam();
        dataSourceParam.setType(param.getSourceType());
        dataSourceParam.setUrl(SOURCE_URL);
        dataSourceParam.setUserName(SOURCE_USERNAME);
        dataSourceParam.setPassword(SOURCE_PASSWORD);
        dataSourceParam.setDriverName(DRIVER_CLASS_NAME);
        param.setSourceDataParam(JSONUtil.toJsonStr(dataSourceParam));
    }
}
