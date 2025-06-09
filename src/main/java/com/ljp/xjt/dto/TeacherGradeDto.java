package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author ljp
 * @version 1.0
 * @since 2025-06-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "教师视角下的学生成绩数据传输对象")
public class TeacherGradeDto {

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "分数")
    private BigDecimal score;

    @Schema(description = "成绩记录ID (方便更新)")
    private Long gradeId;
} 