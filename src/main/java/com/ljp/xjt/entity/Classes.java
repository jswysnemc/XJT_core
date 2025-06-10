package com.ljp.xjt.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 班级实体类
 * <p>
 * 对应数据库表：classes
 * 存储班级信息，包括班级名称、编码、年级、专业关联等
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("classes")
public class Classes {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;                    // 班级唯一标识

    @TableField("class_name")
    private String className;           // 班级名称

    @TableField("class_code")
    private String classCode;           // 班级编码

    @TableField("grade_year")
    private Integer gradeYear;          // 年级（入学年份）

    @TableField("major_id")
    private Long majorId;               // 专业ID，外键

    @TableField("advisor_teacher_id")
    private Long advisorTeacherId;      // 班主任教师ID，外键

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;  // 创建时间

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;  // 更新时间

} 