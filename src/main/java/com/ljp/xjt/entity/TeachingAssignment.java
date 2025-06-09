package com.ljp.xjt.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教学安排实体类
 * <p>
 * 映射数据库中的 teaching_assignments 表，记录教师、课程和班级之间的授课关系。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("teaching_assignments")
public class TeachingAssignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("teacher_id")
    private Long teacherId;

    @TableField("course_id")
    private Long courseId;

    @TableField("class_id")
    private Long classId;

    @TableField("semester")
    private String semester;

    @TableField("year")
    private Integer year;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
} 