package com.ljp.xjt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * <p>
 * 班级信息数据传输对象
 * </p>
 *
 * @author ljp
 * @since 2025-06-09
 */
@Data
public class ClassDto {
    private Long id;
    private String className;
    private String classCode;
    private Integer gradeYear;
    private Long majorId;
    private String majorName; // 新增的专业名称字段
    private Long advisorTeacherId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
} 