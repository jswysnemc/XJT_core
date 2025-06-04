package com.ljp.xjt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 教师课程关联实体类
 * <p>
 * 对应数据库表：teacher_course
 * 存储教师与课程的教授关系
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("teacher_course")
public class TeacherCourse {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;                    // 主键ID

    @TableField("teacher_id")
    private Long teacherId;             // 教师ID，外键

    @TableField("course_id")
    private Long courseId;              // 课程ID，外键

    @TableField("class_id")
    private Long classId;               // 班级ID，外键

    @TableField("semester")
    private String semester;            // 学期

    @TableField("year")
    private Integer year;               // 学年

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;  // 创建时间

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;  // 更新时间
} 