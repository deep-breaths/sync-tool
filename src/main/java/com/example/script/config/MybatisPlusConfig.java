package com.example.script.config;

/**
 * @author albert lewis
 * @date 2023/12/26
 */

/**
 * mybatisplus 配置
 */
//@Configuration(proxyBeanMethods = false)
public class MybatisPlusConfig {

//    /**
//     * 新的分页插件,一缓和二缓遵循mybatis的规则,
//     */
//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
//        return interceptor;
//    }
//    @Bean
//    public GlobalConfig globalConfig() {
//        GlobalConfig globalConfig = new GlobalConfig();
//        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());
//        return globalConfig;
//    }
//    @Bean
//    public ConfigurationCustomizer configurationCustomizer() {
//        return configuration -> {
//            // 注册自定义的 TypeHandler
//            configuration.getTypeHandlerRegistry().register(StringListTypeHandler.class);
//        };
//    }
}