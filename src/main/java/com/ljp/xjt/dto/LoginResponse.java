package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 * <p>
 * 封装用户登录成功后返回的JWT和用户基本信息。
 * </p>
 * 
 * @author ljp
 * @version 1.1
 * @since 2025-05-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应参数")
public class LoginResponse {

    @Schema(description = "JWT访问令牌", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTY3OTg4ODAwMH0.abcdef")
    private String token;
    
    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTY4MDg4ODAwMH0.ghijkl")
    private String refreshToken;
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Schema(description = "用户角色", example = "ADMIN")
    private String role;
    
    // 简化构造函数，仅提供令牌
    public LoginResponse(String token) {
        this.token = token;
    }
    
    // 提供令牌和刷新令牌的构造函数
    public LoginResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
} 