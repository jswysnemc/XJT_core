package com.ljp.xjt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置类（临时配置 - 禁用认证用于测试）
 * <p>
 * 临时禁用Spring Security认证功能，便于测试API接口和文档访问
 * 后续将实现完整的JWT认证和权限控制
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置安全过滤器链（临时配置 - 允许所有请求）
     *
     * @param http HttpSecurity配置对象
     * @return 安全过滤器链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. 禁用CSRF保护
            .csrf(AbstractHttpConfigurer::disable)
            
            // 2. 禁用CORS
            .cors(AbstractHttpConfigurer::disable)
            
            // 3. 允许所有请求（临时配置）
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            );

        return http.build();
    }

} 