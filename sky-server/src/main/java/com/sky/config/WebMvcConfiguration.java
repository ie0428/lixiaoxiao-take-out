package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.interceptor.JwtTokenUserInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;



import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;
    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login")
                .excludePathPatterns("/doc.html")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/swagger-resources")
                .excludePathPatterns("/v2/api-docs");


        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/user/login")
                .excludePathPatterns("/user/shop/status")
                .excludePathPatterns("/doc.html")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/swagger-resources")
                .excludePathPatterns("/v2/api-docs");
    }

    /**
     * 通过knife4j生成接口文档
     *
     * @return
     */
    @Configuration
    @Slf4j
    public static class Knife4jConfiguration {
        /**
         * 管理端接口文档
         *
         * @return
         */
        @Bean
        public GroupedOpenApi adminApi() {
            log.info("准备生成接口文档");
            return GroupedOpenApi.builder()  // 创建了一个api接口的分组
                    .group("管理端接口")         // 分组名称
                    .pathsToMatch("/admin/**")  // 接口请求路径规则
                    .build();
        }

        /**
         * 用户端接口文档
         *
         * @return
         */
        @Bean
        public GroupedOpenApi userApi() {
            log.info("准备生成接口文档");
            return GroupedOpenApi.builder()  // 创建了一个api接口的分组
                    .group("用户端接口")         // 分组名称
                    .pathsToMatch("/user/**")  // 接口请求路径规则
                    .build();
        }

        @Bean
        public OpenAPI openAPI() {
            return new OpenAPI()
                    .info(new Info()
                            .title("黎小小食铺接口文档")
                            .description("黎小小食铺接口文档")
                            .version("v1")
                            .contact(new Contact().name("robin").email("robin@gmail.com"))
                            .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                    );
        }


        /**
         * 设置静态资源映射
         *
         * @param registry
         */

        protected void addResourceHandlers(ResourceHandlerRegistry registry) {
            log.info("开始设置静态资源映射...");
            registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        }

        /**
         * 扩展消息转换器,将日期类型从列表转换为时间戳
         * 这个是导致knife4j不能正常显示的罪魁祸首,特别要注意添加的位置
         *
         * @param converters 消息转换器列表
         */

        protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
            jackson2HttpMessageConverter.setObjectMapper(new JacksonObjectMapper());
            converters.add(converters.size() - 1, jackson2HttpMessageConverter);
        }

    }
}
