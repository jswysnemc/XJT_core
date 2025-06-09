package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 教学统计数据传输对象
 * <p>
 * 用于封装教师的教学相关统计信息。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "教学统计信息")
public class TeachingStatisticsDto {

    @Schema(description = "教授的课程总数", example = "5")
    private Long totalCourses;

    @Schema(description = "教授的班级总数", example = "8")
    private Long totalClasses;

    @Schema(description = "教授的学生总数", example = "240")
    private Long totalStudents;

    @Schema(description = "所有学生的平均分", example = "85.50")
    private BigDecimal averageScore;
} 