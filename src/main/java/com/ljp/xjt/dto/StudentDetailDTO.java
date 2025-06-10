package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 学生详细信息数据传输对象
 * <p>
 * 用于展示学生的详细信息，包含其所在的班级名称。
 * </p>
 *
 * @author ljp
 * @since 2025-06-10
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "学生详细信息，包含班级名称")
public class StudentDetailDTO {

    @Schema(description = "学生ID")
    private Long id;

    @Schema(description = "关联的用户ID")
    private Long userId;

    @Schema(description = "学号")
    private String studentNumber;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "性别：0-女，1-男")
    private Integer gender;

    @Schema(description = "出生日期")
    private LocalDate birthDate;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

} 