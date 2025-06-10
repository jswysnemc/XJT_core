package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 班级课程成绩分析的完整响应数据 DTO
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
@Schema(description = "班级课程成绩分析的完整响应数据")
public class ClassGradeAnalysisDTO {

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "核心统计指标对象")
    private GradeStatisticsDTO statistics;

    @Schema(description = "成绩分数段分布的数组")
    private List<GradeDistributionBucketDTO> distribution;
} 