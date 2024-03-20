package com.example.script.config;

import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.core.conditions.interfaces.Nested;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.SqlSession;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.util.Collections;

@Configuration
@ImportRuntimeHints(MybatisPlusRuntimeHintsRegistrar.class)
public class MybatisPlusRuntimeHintsRegistrar implements RuntimeHintsRegistrar {


    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.proxies()
             .registerJdkProxy(Func.class)
             .registerJdkProxy(Join.class)
             .registerJdkProxy(Query.class)
             .registerJdkProxy(IPage.class)
             .registerJdkProxy(Nested.class)
             .registerJdkProxy(Compare.class)
             .registerJdkProxy(Executor.class)
             .registerJdkProxy(IService.class)
             .registerJdkProxy(SqlSession.class)
             .registerJdkProxy(StatementHandler.class)
        .registerJdkProxy(ResultSetHandler.class)
        .registerJdkProxy(ParameterHandler.class)
        ;
        hints.serialization().registerType(SerializedLambda.class);
        hints.serialization().registerType(SFunction.class);
        hints.serialization().registerType(java.lang.invoke.SerializedLambda.class);
        hints.reflection().registerType(SFunction.class);
        hints.reflection().registerType(SerializedLambda.class);
        hints.reflection().registerType(java.lang.invoke.SerializedLambda.class);
        hints.reflection()
             .registerType(Wrapper.class, builder -> builder.withMembers(MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_PUBLIC_METHODS))
             .registerType(Wrappers.class, builder -> builder.withMembers(MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_PUBLIC_METHODS))
             .registerType(MybatisXMLLanguageDriver.class, builder -> builder.withMethod("<init>", Collections.emptyList(), ExecutableMode.INVOKE))
             .registerType(QueryWrapper.class, builder -> builder.withMembers(MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_PUBLIC_METHODS))
             .registerType(AbstractWrapper.class, builder -> builder.withMembers(MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_PUBLIC_METHODS))
             .registerType(AbstractChainWrapper.class, builder -> builder.withMembers(MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_PUBLIC_METHODS))
             .registerType(Page.class, builder -> builder.withMembers(MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS))
             .registerType(BoundSql.class, builder -> builder.withMembers(MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS, MemberCategory.DECLARED_FIELDS))
        ;
        hints.reflection().registerType(LambdaQueryWrapper.class, MemberCategory.values());
        hints.reflection().registerType(LambdaUpdateWrapper.class, MemberCategory.values());
        hints.reflection().registerType(UpdateWrapper.class, MemberCategory.values());

    }

}