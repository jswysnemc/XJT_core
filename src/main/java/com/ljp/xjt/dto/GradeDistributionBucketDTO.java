package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 成绩分布区间定义DTO
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "成绩分布区间定义")
public class GradeDistributionBucketDTO {

    @Schema(description = "分数段标签，由后端定义 (例如: \"90-100分 (优秀)\")")
    private String range;

    @Schema(description = "该分数段对应的人数")
    private long count;
} 