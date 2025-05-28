package com.ljp.xjt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩实体类
 * <p>
 * 对应数据库表：grades
 * 存储学生的课程成绩记录，是系统的核心业务数据
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("grades")
public class Grade {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;                    // 成绩唯一标识

    @TableField("student_id")
    private Long studentId;             // 学生ID，外键

    @TableField("course_id")
    private Long courseId;              // 课程ID，外键

    @TableField("score")
    private BigDecimal score;           // 成绩分数

    @TableField("grade_type")
    private String gradeType;           // 成绩类型：MIDTERM-期中，FINAL-期末，HOMEWORK-作业

    @TableField("semester")
    private String semester;            // 学期

    @TableField("year")
    private Integer year;               // 学年

    @TableField("is_abnormal")
    private Integer isAbnormal;         // 是否异常：0-正常，1-异常

    @TableField("remarks")
    private String remarks;             // 备注

    @TableField("created_by")
    private Long createdBy;             // 创建者用户ID，外键

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;  // 创建时间

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;  // 更新时间

} 