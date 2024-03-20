package com.example.script.config;

import com.example.script.local.web.ExportController;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeSerialization;

/**
 * @author albert lewis
 * @date 2024/3/19
 */
public class LambdaRegistrationFeature implements Feature {

    @Override
    public void duringSetup(Feature.DuringSetupAccess access) {


        // TODO 这里需要将lambda表达式所使用的成员类都注册上来,具体情况视项目情况而定,一般扫描@Controller和@Service的会多点.
        RuntimeSerialization.registerLambdaCapturingClass(ExportController.class);
    }
}