package com.ljp.xjt.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljp.xjt.common.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT访问拒绝处理器
 * <p>
 * 当已认证用户尝试访问其没有权限的资源时，此类将处理访问被拒绝的情况，
 * 并返回一个标准的JSON格式的403 Forbidden响应。
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("Access denied for user {} to {} from IP {}: {}", 
                 request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous",
                 request.getRequestURI(), 
                 request.getRemoteAddr(), 
                 accessDeniedException.getMessage());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ApiResponse<Object> apiResponse = ApiResponse.error(HttpStatus.FORBIDDEN.value(), "您没有权限访问此资源");
        
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
} 