package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 批量成绩导入响应的数据传输对象
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-11
 */
@Data
@Builder
@Schema(description = "批量成绩导入的响应数据")
public class BatchGradeResponseDto {

    @Schema(description = "成功处理的条目数", example = "2")
    private int successCount;

    @Schema(description = "处理失败的条目数", example = "1")
    private int failureCount;

    @Schema(description = "失败条目的详细信息")
    private List<FailureDetailDto> failures;
} 