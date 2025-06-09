package com.ljp.xjt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 学生个人信息更新数据传输对象
 * <p>
 * 用于学生更新个人信息时传输数据。
 * 包含可被学生修改的字段，并附带了JSR 303校验注解。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
public class StudentProfileUpdateDTO {

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Size(max = 20, message = "手机号码长度不能超过20个字符")
    private String phone;

    @Size(min = 2, max = 50, message = "姓名长度必须在2到50个字符之间")
    private String studentName;

    @Min(value = 0, message = "性别代码不正确")
    @Max(value = 1, message = "性别代码不正确")
    private Integer gender;
} 