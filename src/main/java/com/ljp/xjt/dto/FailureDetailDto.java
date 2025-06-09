package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 批量导入失败详情的数据传输对象
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "失败条目的详细信息")
public class FailureDetailDto {

    @Schema(description = "失败条目的学号", example = "99999999")
    private String studentNumber;

    @Schema(description = "失败条目的成绩", example = "95")
    private BigDecimal score;

    @Schema(description = "失败原因", example = "该学号不存在于本班级中")
    private String reason;
} 