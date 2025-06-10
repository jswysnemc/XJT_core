package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 成绩统计核心指标DTO
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "成绩统计核心指标")
public class GradeStatisticsDTO {

    @Schema(description = "平均分")
    private double averageScore;

    @Schema(description = "最高分")
    private double highestScore;

    @Schema(description = "最低分")
    private double lowestScore;

    @Schema(description = "及格率 (0 到 1 之间的小数, 例如 0.95 表示 95%)")
    private double passingRate;

    @Schema(description = "已录入成绩的学生人数")
    private long evaluatedStudentCount;

    @Schema(description = "班级总学生人数")
    private long totalStudentCount;
} 