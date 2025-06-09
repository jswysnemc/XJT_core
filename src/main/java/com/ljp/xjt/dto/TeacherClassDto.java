package com.ljp.xjt.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 教师在特定课程下所授班级信息数据传输对象
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherClassDto {

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 班级名称
     */
    private String className;
    
    /**
     * 年级
     */
    private Integer gradeYear;
} 