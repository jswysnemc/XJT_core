package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 单个成绩更新请求数据传输对象
 * <p>
 * 用于封装教师修改或录入单个学生成绩时提交的数据。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
@Schema(description = "单个成绩更新请求体")
public class ScoreUpdateRequest {

    @Schema(description = "课程成绩，范围为0.00-100.00", requiredMode = Schema.RequiredMode.REQUIRED, example = "88.50")
    @NotNull(message = "分数不能为空")
    @DecimalMin(value = "0.0", inclusive = true, message = "分数不能低于0.0")
    @DecimalMax(value = "100.0", inclusive = true, message = "分数不能高于100.0")
    @Digits(integer = 3, fraction = 2, message = "分数格式不正确，整数最多3位，小数最多2位")
    private BigDecimal score;
} 