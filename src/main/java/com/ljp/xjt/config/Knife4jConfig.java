package com.ljp.xjt.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类
 * <p>
 * 配置Knife4j OpenAPI3文档，提供接口文档和在线测试功能
 * 支持JWT认证，按模块分组展示API接口，适配Spring Boot 3.x
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Configuration
public class Knife4jConfig {

    /**
     * OpenAPI基础信息配置
     *
     * @return OpenAPI配置对象
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("学生成绩管理系统API")
                        .description("学生成绩管理系统的RESTful API文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ljp")
                                .email("ljp@example.com")))
                .components(new Components()
                        .addSecuritySchemes("Bearer", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT认证")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"));
    }

    /**
     * 全局API分组 - 显示所有接口
     *
     * @return 全局API分组
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("00-全部接口")
                .pathsToMatch("/**")
                .packagesToScan("com.ljp.xjt.controller")
                .build();
    }

    /**
     * 认证模块API分组
     *
     * @return 测试信息模块API分组
     */
    @Bean
    public GroupedOpenApi infoApi() {
        return GroupedOpenApi.builder()
                .group("00-信息模块")
                .pathsToMatch("/test/**")
                .packagesToScan("com.ljp.xjt.controller")
                .build();
    }

    /**
     * 认证模块API分组
     *
     * @return 认证模块API分组
     */
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("01-认证模块")
                .pathsToMatch("/auth/**")
                .packagesToScan("com.ljp.xjt.controller")
                .build();
    }

    /**
     * 学生模块API分组
     *
     * @return 学生模块API分组
     */
    @Bean
    public GroupedOpenApi studentApi() {
        return GroupedOpenApi.builder()
                .group("02-学生模块")
                .pathsToMatch("/student/**")
                .packagesToScan("com.ljp.xjt.controller")
                .build();
    }

    /**
     * 教师模块API分组
     *
     * @return 教师模块API分组
     */
    @Bean
    public GroupedOpenApi teacherApi() {
        return GroupedOpenApi.builder()
                .group("03-教师模块")
                .pathsToMatch("/teacher/**")
                .packagesToScan("com.ljp.xjt.controller")
                .build();
    }

    /**
     * 管理员模块API分组
     *
     * @return 管理员模块API分组
     */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("04-管理员模块")
                .pathsToMatch("/admin/**")
                .packagesToScan("com.ljp.xjt.controller")
                .build();
    }

    /**
     * 公共模块API分组
     *
     * @return 公共模块API分组
     */
    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
                .group("05-公共模块")
                .pathsToMatch("/common/**", "/file/**", "/test/**")
                .packagesToScan("com.ljp.xjt.controller")
                .build();
    }

} 