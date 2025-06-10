package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 学生创建数据传输对象
 * <p>
 * 用于管理员创建学生档案，仅包含学生基本信息。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
@Schema(description = "学生创建请求体")
public class StudentCreateDTO {

    @NotBlank(message = "学号不能为空")
    @Pattern(regexp = "^\\d{8,20}$", message = "学号必须为8到20位的数字")
    @Schema(description = "学号", example = "2025010101")
    private String studentNumber;

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "学生姓名", example = "张三")
    private String studentName;

    @NotNull(message = "性别不能为空")
    @Schema(description = "性别：0-女，1-男", example = "1")
    private Integer gender;

    @Past(message = "出生日期必须是过去的时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "出生日期 (可选)", example = "2007-01-01")
    private LocalDate birthDate;

} 