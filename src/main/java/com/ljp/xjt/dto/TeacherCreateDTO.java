package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 创建教师的数据传输对象
 * <p>
 * 用于管理员创建新教师档案时，接收前端传递的数据。
 * 此时只包含教师的基本信息，不包含用户ID。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Data
@Schema(description = "创建教师的数据传输对象")
public class TeacherCreateDTO {

    @Schema(description = "教工号", requiredMode = Schema.RequiredMode.REQUIRED, example = "T2025001")
    @NotBlank(message = "教工号不能为空")
    @Length(min = 4, max = 20, message = "教工号长度必须在4到20个字符之间")
    private String teacherNumber;

    @Schema(description = "教师姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "教师姓名不能为空")
    @Length(min = 2, max = 50, message = "教师姓名长度必须在2到50个字符之间")
    private String teacherName;

    @Schema(description = "性别：0-女，1-男", example = "1")
    @NotNull(message = "性别不能为空")
    private Integer gender;

    @Schema(description = "职称", example = "教授")
    @Length(max = 30, message = "职称长度不能超过30个字符")
    private String title;

    @Schema(description = "所属部门ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "所属部门ID不能为空")
    @Positive(message = "部门ID必须为正数")
    private Long departmentId;

} 