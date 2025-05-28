package com.ljp.xjt.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljp.xjt.common.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT认证入口点
 * <p>
 * 当未认证用户尝试访问受保护资源时，此类将处理认证失败的情况，
 * 并返回一个标准的JSON格式的401 Unauthorized响应。
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.warn("Unauthorized access attempt to {} from IP {}: {}", 
                 request.getRequestURI(), request.getRemoteAddr(), authException.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ApiResponse<Object> apiResponse = ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "用户未认证或认证已过期，请重新登录");
        
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
} 