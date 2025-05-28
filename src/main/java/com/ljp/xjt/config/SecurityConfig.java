package com.ljp.xjt.config;

import com.ljp.xjt.security.handler.JwtAccessDeniedHandler;
import com.ljp.xjt.security.handler.JwtAuthenticationEntryPoint;
import com.ljp.xjt.security.jwt.JwtAuthenticationFilter;
import com.ljp.xjt.service.UserService;
import com.ljp.xjt.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类
 * <p>
 * 配置Spring Security，包括用户认证服务、密码编码器、JWT认证、自定义异常处理以及HTTP安全规则。
 * 启用方法级别的安全注解支持。
 * </p>
 * 
 * @author ljp
 * @version 1.5
 * @since 2025-05-29
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * 配置密码编码器
     *
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置自定义的UserDetailsService
     * 这里使用@Lazy确保它在需要时才被创建，帮助解决循环依赖
     */
    @Bean
    @Lazy
    public UserDetailsServiceImpl userDetailsServiceImpl(UserService userService) {
        return new UserDetailsServiceImpl(userService);
    }

    /**
     * 配置认证提供者 DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsSvc, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsSvc);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * 从 AuthenticationConfiguration 获取 AuthenticationManager
     * Spring Boot 2.7+ 的推荐方式
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置安全过滤器链
     *
     * @param http HttpSecurity配置对象
     * @return 安全过滤器链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, 
                                           DaoAuthenticationProvider daoAuthenticationProvider, 
                                           JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .authenticationProvider(daoAuthenticationProvider)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable) 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/doc.html", "/webjars/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/auth/**", "/test/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

} 