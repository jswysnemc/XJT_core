package com.ljp.xjt.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 教师所授课程信息数据传输对象
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCourseDto {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程代码
     */
    private String courseCode;

    /**
     * 学分
     */
    private BigDecimal credits;
} 