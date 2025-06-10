package com.ljp.xjt.controller;

import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.dto.ClassGradeAnalysisDTO;
import com.ljp.xjt.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计分析控制器
 * <p>
 * 提供各类统计数据查询接口
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@RestController
@RequestMapping("/admin/statistics")
@Tag(name = "管理端 - 统计分析", description = "提供各类统计数据查询接口")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 获取班级课程成绩分析数据
     *
     * @param classId  班级ID
     * @param courseId 课程ID
     * @return ApiResponse<ClassGradeAnalysisDTO> 包含完整分析数据的DTO
     */
    @GetMapping("/class-grade-analysis")
    @Operation(summary = "获取班级课程成绩分析", description = "获取指定班级在某一门课程上的详细成绩统计和分布数据。")
    public ApiResponse<ClassGradeAnalysisDTO> getClassGradeAnalysis(
            @Parameter(description = "要查询的班级ID", required = true) @RequestParam @NotNull Long classId,
            @Parameter(description = "要查询的课程ID", required = true) @RequestParam @NotNull Long courseId) {
        
        ClassGradeAnalysisDTO analysisData = statisticsService.getClassGradeAnalysis(classId, courseId);
        return ApiResponse.success(analysisData);
    }

} 