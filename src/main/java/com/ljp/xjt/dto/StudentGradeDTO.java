package com.ljp.xjt.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * 学生成绩数据传输对象
 * <p>
 * 用于封装学生查询个人成绩时返回的数据，包含了成绩、课程、教师及学期等详细信息。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentGradeDTO {

    /**
     * 学期
     */
    private String semester;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 授课教师姓名
     */
    private String teacherName;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 成绩分数
     */
    private BigDecimal score;

    /**
     * 课程学分
     */
    private BigDecimal credits;

    /**
     * 课程学时
     */
    private Integer courseHours;
} 