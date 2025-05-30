package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新令牌请求DTO
 * <p>
 * 封装用户请求刷新访问令牌时提交的参数。
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Data
@Schema(description = "刷新令牌请求参数")
public class RefreshTokenRequest {

    @NotBlank(message = "刷新令牌不能为空")
    @Schema(description = "刷新令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "eyJhbGciOiJIUzUxMiJ9...")
    private String refreshToken;
    
} 