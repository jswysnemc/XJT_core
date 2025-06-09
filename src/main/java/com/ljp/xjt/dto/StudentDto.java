package com.ljp.xjt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 学生数据传输对象
 * <p>
 * 用于在API层传输学生的基本信息，不涉及敏感或详细数据。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学生简要信息")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class StudentDto {

    @Schema(description = "学生ID", example = "1")
    private Long studentId;

    @Schema(description = "学号", example = "20230001")
    private String studentNumber;

    @Schema(description = "学生姓名", example = "张三")
    private String studentName;

    @Schema(description = "课程名称", example = "大学物理")
    private String courseName;

    @Schema(description = "班级名称", example = "物理2301班")
    private String className;

    @Schema(description = "课程成绩", example = "88.50")
    private BigDecimal score;
} 