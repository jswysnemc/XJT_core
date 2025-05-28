package com.ljp.xjt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 教师实体类
 * <p>
 * 对应数据库表：teachers
 * 存储教师特有信息，与用户表通过user_id关联
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("teachers")
public class Teacher {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;                    // 教师唯一标识

    @TableField("user_id")
    private Long userId;                // 用户ID，外键

    @TableField("teacher_number")
    private String teacherNumber;       // 教工号

    @TableField("teacher_name")
    private String teacherName;         // 教师姓名

    @TableField("gender")
    private Integer gender;             // 性别：0-女，1-男

    @TableField("title")
    private String title;               // 职称

    @TableField("department_id")
    private Long departmentId;          // 部门ID，外键

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;  // 创建时间

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;  // 更新时间

} 