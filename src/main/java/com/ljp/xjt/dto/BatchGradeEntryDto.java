package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 批量成绩录入条目数据传输对象
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-11
 */
@Data
@Schema(description = "批量成绩录入条目")
public class BatchGradeEntryDto {

    @Schema(description = "学生的学号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20230101")
    @NotBlank(message = "学号不能为空")
    private String studentNumber;

    @Schema(description = "课程成绩 (0-100)", requiredMode = Schema.RequiredMode.REQUIRED, example = "88.5")
    @NotNull(message = "成绩不能为空")
    @DecimalMin(value = "0.0", message = "成绩不能低于0分")
    @DecimalMax(value = "100.0", message = "成绩不能高于100分")
    private BigDecimal score;
} 