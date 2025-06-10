package com.ljp.xjt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 从班级移除学生请求体 DTO
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
@Schema(description = "从班级移除学生请求体")
public class RemoveStudentsDTO {

    @NotEmpty(message = "学生ID列表不能为空")
    @Schema(description = "待移除的学生ID列表", required = true)
    private List<Long> studentIds;
}
