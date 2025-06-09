package com.ljp.xjt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 教师视图下的学生成绩信息数据传输对象
 * <p>
 * 用于展示给教师的、包含学生基本信息和其单科成绩的成绩单。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-09
 */
@Data
public class StudentGradeDto {

    /**
     * 成绩ID (如果成绩已存在，则非空)
     */
    private Long gradeId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 分数
     */
    private BigDecimal score;

    /**
     * 成绩最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gradeUpdatedTime;
} 