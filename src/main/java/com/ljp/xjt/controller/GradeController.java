package com.ljp.xjt.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.entity.Grade;
import com.ljp.xjt.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 成绩管理控制器（管理员视角）
 * <p>
 * 提供成绩管理相关的API接口，包括成绩查询、审核等功能
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Slf4j
@RestController
@RequestMapping("/admin/grades")
@RequiredArgsConstructor
@Validated
@Tag(name = "成绩管理", description = "管理员成绩管理相关接口")
@PreAuthorize("hasRole('ADMIN')")
public class GradeController {

    private final GradeService gradeService;

    /**
     * 分页查询成绩列表
     *
     * @param current 当前页码
     * @param size 每页大小
     * @param classId 班级ID（可选）
     * @param courseId 课程ID（可选）
     * @param semester 学期（可选）
     * @param year 学年（可选）
     * @return 成绩分页数据
     */
    @GetMapping
    @Operation(summary = "分页查询成绩列表", description = "管理员分页查询成绩")
    public ApiResponse<IPage<Grade>> getGradeList(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") @Positive Long current,
            
            @Parameter(description = "每页大小", example = "10") 
            @RequestParam(defaultValue = "10") @Positive Long size,
            
            @Parameter(description = "班级ID") 
            @RequestParam(required = false) Long classId,
            
            @Parameter(description = "课程ID") 
            @RequestParam(required = false) Long courseId,
            
            @Parameter(description = "学期", example = "2025-Spring") 
            @RequestParam(required = false) String semester,
            
            @Parameter(description = "学年", example = "2025") 
            @RequestParam(required = false) Integer year) {
        
        log.info("Query grade list - current: {}, size: {}, classId: {}, courseId: {}, semester: {}, year: {}", 
                 current, size, classId, courseId, semester, year);
        
        Page<Grade> page = new Page<>(current, size);
        IPage<Grade> gradePage = gradeService.getGradeList(page, classId, courseId, semester, year);
        
        return ApiResponse.success("查询成功", gradePage);
    }

    /**
     * 获取成绩详情
     *
     * @param id 成绩ID
     * @return 成绩详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取成绩详情", description = "根据ID获取成绩详情")
    public ApiResponse<Grade> getGradeById(
            @Parameter(description = "成绩ID", required = true)
            @PathVariable @NotNull @Positive Long id) {
        
        log.info("Get grade by id: {}", id);
        
        Grade grade = gradeService.getGradeById(id);
        if (grade == null) {
            return ApiResponse.notFound();
        }
        
        return ApiResponse.success("查询成功", grade);
    }

    /**
     * 审核成绩（标记或取消标记为异常）
     *
     * @param id 成绩ID
     * @param isAbnormal 是否异常（1-异常，0-正常）
     * @param remarks 备注
     * @return 操作结果
     */
    @PutMapping("/{id}/review")
    @Operation(summary = "审核成绩", description = "管理员审核成绩（标记或取消标记为异常）")
    public ApiResponse<Void> reviewGrade(
            @Parameter(description = "成绩ID", required = true)
            @PathVariable @NotNull @Positive Long id,
            
            @Parameter(description = "是否异常（1-异常，0-正常）", required = true, example = "0")
            @RequestParam @NotNull Integer isAbnormal,
            
            @Parameter(description = "备注")
            @RequestParam(required = false) String remarks) {
        
        log.info("Review grade: {}, isAbnormal: {}, remarks: {}", id, isAbnormal, remarks);
        
        // 验证是否存在
        Grade grade = gradeService.getById(id);
        if (grade == null) {
            return ApiResponse.notFound();
        }
        
        try {
            boolean result = gradeService.reviewGrade(id, isAbnormal, remarks);
            if (!result) {
                return ApiResponse.error("审核失败");
            }
            
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除成绩
     *
     * @param id 成绩ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除成绩", description = "管理员删除成绩")
    public ApiResponse<Void> deleteGrade(
            @Parameter(description = "成绩ID", required = true)
            @PathVariable @NotNull @Positive Long id) {
        
        log.info("Delete grade: {}", id);
        
        // 验证是否存在
        Grade grade = gradeService.getById(id);
        if (grade == null) {
            return ApiResponse.notFound();
        }
        
        boolean result = gradeService.removeById(id);
        if (!result) {
            return ApiResponse.error("删除失败");
        }
        
        return ApiResponse.success();
    }

    /**
     * 获取班级课程成绩统计
     *
     * @param classId 班级ID
     * @param courseId 课程ID
     * @param semester 学期（可选）
     * @param year 学年（可选）
     * @return 成绩统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取成绩统计", description = "获取班级课程成绩统计信息")
    public ApiResponse<Map<String, Object>> getGradeStatistics(
            @Parameter(description = "班级ID", required = true)
            @RequestParam @NotNull Long classId,
            
            @Parameter(description = "课程ID", required = true)
            @RequestParam @NotNull Long courseId,
            
            @Parameter(description = "学期", example = "2025-Spring")
            @RequestParam(required = false) String semester,
            
            @Parameter(description = "学年", example = "2025")
            @RequestParam(required = false) Integer year) {
        
        log.info("Get grade statistics - classId: {}, courseId: {}, semester: {}, year: {}", 
                 classId, courseId, semester, year);
        
        Map<String, Object> statistics = gradeService.getGradeStatistics(classId, courseId, semester, year);
        
        return ApiResponse.success("查询成功", statistics);
    }
}