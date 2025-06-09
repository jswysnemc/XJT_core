package com.ljp.xjt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员端成绩列表数据传输对象
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-11
 */
@Data
@Schema(description = "管理员端成绩视图")
public class AdminGradeDto {

    @Schema(description = "成绩记录ID", example = "1")
    private Long id;

    @Schema(description = "学生姓名", example = "张三")
    private String studentName;

    @Schema(description = "学号", example = "20230101")
    private String studentNumber;

    @Schema(description = "班级名称", example = "软件工程2023级1班")
    private String className;

    @Schema(description = "课程名称", example = "计算机网络")
    private String courseName;

    @Schema(description = "授课教师", example = "李四")
    private String teacherName;

    @Schema(description = "分数", example = "88.50")
    private BigDecimal score;

    @Schema(description = "绩点", example = "3.7")
    private BigDecimal gpa;

    @Schema(description = "成绩是否正常 (例如，及格)", example = "true")
    private boolean isNormal;
    
    @Schema(description = "成绩是否已复核", example = "false")
    private boolean isReviewed;

    @Schema(description = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
} 