package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教师个人资料数据传输对象
 * <p>
 * 用于展示教师的详细个人信息，包括关联的部门名称。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "教师个人详细资料")
public class TeacherProfileDto {

    @Schema(description = "教师ID", example = "1")
    private Long teacherId;

    @Schema(description = "教工号", example = "T001")
    private String teacherNumber;

    @Schema(description = "教师姓名", example = "王老师")
    private String teacherName;

    @Schema(description = "性别 (0: 未知, 1: 男, 2: 女)", example = "1")
    private Integer gender;

    @Schema(description = "职称", example = "教授")
    private String title;

    @Schema(description = "所属部门ID", example = "1")
    private Long departmentId;

    @Schema(description = "所属部门名称", example = "计算机科学与技术学院")
    private String departmentName;

    @Schema(description = "关联的用户ID", example = "10")
    private Long userId;

    @Schema(description = "用户邮箱", example = "teacher@example.com")
    private String email;

    @Schema(description = "用户手机号", example = "13800138000")
    private String phone;
} 