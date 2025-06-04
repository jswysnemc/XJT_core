package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 令牌验证响应DTO
 * <p>
 * 包含令牌有效性及相关详细信息。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "令牌验证响应")
public class ValidateTokenResponse {

    @Schema(description = "令牌是否有效", example = "true")
    private boolean valid;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "用户角色", example = "ADMIN")
    private String role;

    @Schema(description = "令牌签发时间")
    private LocalDateTime issuedAt;
    
    @Schema(description = "令牌到期时间")
    private LocalDateTime expiresAt;

    @Schema(description = "令牌剩余有效秒数", example = "3600")
    private Long remainingSeconds;
} 