package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 教学分配信息数据传输对象
 * <p>
 * 用于展示排课列表，包含关联的教师、课程和班级名称。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "教学分配详细信息")
public class TeachingAssignmentDto {

    @Schema(description = "排课ID")
    private Long id;

    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "学期", example = "2024-2025-1")
    private String semester;

    @Schema(description = "学年", example = "2024")
    private Integer year;
} 