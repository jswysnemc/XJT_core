package com.ljp.xjt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 课程安排实体类
 * <p>
 * 对应数据库表：course_schedules
 * 存储课程安排信息，关联班级、课程和教师
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("course_schedules")
public class CourseSchedule {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;                    // 安排唯一标识

    @TableField("course_id")
    private Long courseId;              // 课程ID，外键

    @TableField("class_id")
    private Long classId;               // 班级ID，外键

    @TableField("teacher_id")
    private Long teacherId;             // 教师ID，外键

    @TableField("semester")
    private String semester;            // 学期

    @TableField("year")
    private Integer year;               // 学年

    @TableField("schedule_time")
    private String scheduleTime;        // 课程时间安排，例如"周一1-2节"

    @TableField("classroom")
    private String classroom;           // 教室

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;  // 创建时间

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;  // 更新时间
} 