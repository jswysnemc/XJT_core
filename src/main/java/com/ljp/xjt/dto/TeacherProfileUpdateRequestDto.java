package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 教师个人资料更新请求的数据传输对象
 * <p>
 * 用于教师更新自己的个人信息，只包含允许修改的字段。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
@Schema(description = "教师个人资料更新请求")
public class TeacherProfileUpdateRequestDto {

    @Schema(description = "教师姓名", example = "王老师")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String teacherName;

    @Schema(description = "电子邮箱", example = "new.teacher@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Schema(description = "手机号码", example = "13900139000")
    @Size(max = 20, message = "手机号码长度不能超过20个字符")
    private String phone;

    // 未来可以扩展其他允许修改的字段，如联系电话、邮箱等。
    // 为了保持接口的专注性，当前版本只允许修改教师姓名、电子邮箱和手机号码。

} 