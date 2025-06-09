package com.ljp.xjt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 成绩批量更新请求的数据传输对象
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-09
 */
@Data
public class GradeUpdateRequest {

    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    @NotNull(message = "更新的成绩列表不能为空")
    private List<GradeItem> grades;
    
    @Data
    public static class GradeItem {
        @NotNull(message = "学生ID不能为空")
        private Long studentId;

        // 允许分数为null，表示可能只是想创建占位记录
        private BigDecimal score;
    }
} 