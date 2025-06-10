package com.ljp.xjt.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 学生课程信息数据传输对象
 * <p>
 * 用于学生查询个人课表时返回的数据，不包含成绩信息。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
public class StudentCourseDTO {

    /**
     * 学年
     */
    private Integer year;

    /**
     * 学期
     */
    private String semester;

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

    /**
     * 学时
     */
    private Integer courseHours;

    /**
     * 授课教师
     */
    private String teacherName;
} 