package com.ljp.xjt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生数据传输对象
 * <p>
 * 用于在API响应中封装学生信息，包含关联的班级名称。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
@Schema(description = "学生信息数据传输对象")
public class StudentDTO {

    @Schema(description = "学生记录ID")
    private Long id;

    @Schema(description = "关联的用户ID")
    private Long userId;

    @Schema(description = "学号")
    private String studentNumber;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "性别 (1: 男, 2: 女)")
    private Integer gender;

    @Schema(description = "出生日期", example = "2005-03-15")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Schema(description = "班级ID")
    private Long classId;
    
    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;
} 