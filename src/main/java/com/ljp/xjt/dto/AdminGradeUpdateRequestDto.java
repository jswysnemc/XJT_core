package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 管理员修改成绩请求的数据传输对象
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-11
 */
@Data
@Schema(description = "管理员修改成绩请求")
public class AdminGradeUpdateRequestDto {

    @Schema(description = "课程成绩 (0-100)", example = "90.0")
    @NotNull(message = "分数不能为空")
    @DecimalMin(value = "0.0", message = "成绩不能低于0分")
    @DecimalMax(value = "100.0", message = "成绩不能高于100分")
    private BigDecimal score;

    @Schema(description = "是否将成绩标记为已复核", example = "true")
    @NotNull(message = "复核状态不能为空")
    private Boolean isReviewed;
} 