package com.ljp.xjt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体类
 * <p>
 * 对应数据库表：courses
 * 存储课程基础信息，包括课程名称、编码、学分、学时等
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("courses")
public class Course {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;                    // 课程唯一标识

    @TableField("course_name")
    private String courseName;          // 课程名称

    @TableField("course_code")
    private String courseCode;          // 课程编码

    @TableField("credits")
    private BigDecimal credits;         // 学分

    @TableField("hours")
    private Integer hours;              // 学时

    @TableField("description")
    private String description;         // 课程描述

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;  // 创建时间

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;  // 更新时间

} 