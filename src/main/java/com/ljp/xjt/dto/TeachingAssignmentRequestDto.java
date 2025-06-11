package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 教学分配创建/更新请求数据传输对象
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Data
@Schema(description = "创建或更新教学分配的请求体")
public class TeachingAssignmentRequestDto {

    @NotNull(message = "教师ID不能为空")
    @Schema(description = "教师ID", required = true)
    private Long teacherId;

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", required = true)
    private Long courseId;

    @NotNull(message = "班级ID不能为空")
    @Schema(description = "班级ID", required = true)
    private Long classId;

    @NotEmpty(message = "学期不能为空")
    @Schema(description = "学期", example = "2024-2025-1", required = true)
    private String semester;

    @NotNull(message = "学年不能为空")
    @Schema(description = "学年", example = "2024", required = true)
    private Integer year;
} 