package com.ljp.xjt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生实体类
 * <p>
 * 对应数据库表：students
 * 存储学生特有信息，与用户表通过user_id关联
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("students")
public class Student {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;                    // 学生唯一标识

    @TableField("user_id")
    private Long userId;                // 用户ID，外键

    @TableField("student_number")
    private String studentNumber;       // 学号

    @TableField("student_name")
    private String studentName;         // 学生姓名

    @TableField("gender")
    private Integer gender;             // 性别：0-女，1-男

    @TableField("birth_date")
    private LocalDate birthDate;        // 出生日期

    @TableField("class_id")
    private Long classId;               // 班级ID，外键

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;  // 创建时间

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;  // 更新时间

} 