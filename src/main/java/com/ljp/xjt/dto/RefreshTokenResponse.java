package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 刷新令牌响应DTO
 * <p>
 * 封装用户刷新令牌后返回的新访问令牌。
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "刷新令牌响应参数")
public class RefreshTokenResponse {

    @Schema(description = "新的访问令牌", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTY3OTg4ODAwMH0.abcdef")
    private String token;
    
    @Schema(description = "新的刷新令牌", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTY4MDg4ODAwMH0.ghijkl")
    private String refreshToken;
    
} 