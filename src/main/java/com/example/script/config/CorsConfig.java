package com.example.script.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author albert lewis
 * @date 2023/12/26
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有URL
                .allowedOriginPatterns("*")// 设置允许跨域请求的源，这里设置为允许所有源
                .allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS")// 允许的请求方法
                .allowedHeaders("*")// 允许的请求头
                .allowCredentials(true) // 允许携带认证信息（如cookies）
                .maxAge(3600) // 预检请求的缓存时间（单位：秒）
                .exposedHeaders("Authorization");// 允许暴露的响应头
    }

//    @Bean
//    public FilterRegistrationBean<CorsFilter> corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOriginPattern("*"); // 设置允许跨域请求的源，这里设置为允许所有源
//        config.addAllowedHeader("*"); // 允许的请求头
//        config.addAllowedMethod("*"); // 允许的请求方法
//        config.setAllowCredentials(true); // 允许携带认证信息（如cookies）
//        config.setMaxAge(3600L); // 预检请求的缓存时间（单位：秒）
//        source.registerCorsConfiguration("/**", config); // 对所有URL应用上述CORS配置
//        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
//        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // 设置过滤器的优先级为最高
//        return bean;
//    }

    /**
     * addCorsMappings方法用于配置CORS的规则，而corsFilter方法用于注册CORS过滤器。实际上，这两个方法的作用是相同的：都是为了实现跨域资源共享（CORS）的配置。
     *
     *     在Spring框架中，有两种方式可以配置CORS：
     *
     *     使用addCorsMappings方法：这是Spring提供的一种简化配置CORS规则的方式。通过addCorsMappings方法，您可以指定允许跨域请求的源、允许的请求方法、允许的请求头等。这种方式适用于大多数情况下的简单CORS配置。
     *
     *     使用corsFilter方法：这是一种更灵活的方式，可以自定义CORS过滤器的配置。通过corsFilter方法，您可以创建一个CorsFilter实例，并将其注册到Spring的过滤器链中。这种方式适用于需要更复杂的CORS配置或对CORS过滤器有更多的自定义需求的情况。
     *
     *     实际上，在上述代码中，corsFilter方法的作用是创建一个CorsFilter实例，并将其注册到过滤器链中，以确保跨域请求的处理符合CORS的规则。这样做的目的是为了确保CORS规则的生效和灵活性，同时可以方便地进行自定义配置。
     *
     *     因此，如果您只使用了addCorsMappings方法进行简单的CORS配置，并没有特殊的需求或对CORS过滤器进行自定义，那么可以不需要使用corsFilter方法。只使用addCorsMappings方法即可实现基本的CORS规则配置。
     *
     *     但是，如果您需要更复杂的CORS配置，或者需要对CORS过滤器进行自定义，那么可以使用corsFilter方法来创建自定义的CorsFilter实例，并将其注册到过滤器链中。
     *
     *     总的来说，是否需要使用corsFilter方法取决于您对CORS配置的需求和自定义程度。如果只是简单的CORS配置，addCorsMappings方法已经足够了。如果需要更复杂的配置或自定义，可以使用corsFilter方法。
     */
}
