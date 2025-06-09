package com.ljp.xjt.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.dto.AdminGradeDto;
import com.ljp.xjt.dto.AdminGradeUpdateRequestDto;
import com.ljp.xjt.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 管理员成绩管理控制器
 * <p>
 * 提供管理员对学生成绩进行查询、修改、复核等操作的API接口。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-11
 */
@Slf4j
@RestController
@RequestMapping("/admin/grades")
@RequiredArgsConstructor
@Tag(name = "管理员-成绩管理", description = "管理员对学生成绩进行高级管理")
@PreAuthorize("hasRole('ADMIN')")
public class AdminGradeController {

    private final GradeService gradeService;

    @GetMapping
    @Operation(summary = "分页查询成绩列表(管理员)", description = "根据多种条件筛选并分页查询所有学生的成绩记录。")
    public ApiResponse<IPage<AdminGradeDto>> getGrades(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "学生姓名 (模糊查询)") @RequestParam(required = false) String studentName,
            @Parameter(description = "学号 (模糊查询)") @RequestParam(required = false) String studentNumber
    ) {
        Page<AdminGradeDto> page = new Page<>(current, size);
        IPage<AdminGradeDto> gradePage = gradeService.getGradesByAdminCriteria(page, classId, courseId, studentName, studentNumber);
        return ApiResponse.success("查询成功", gradePage);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改成绩(管理员)", description = "修改指定ID的成绩记录，可以更新分数和复核状态。")
    public ApiResponse<Void> updateGrade(
            @Parameter(description = "要修改的成绩记录ID") @PathVariable Long id,
            @Valid @RequestBody AdminGradeUpdateRequestDto updateDto) {
        boolean success = gradeService.adminUpdateGrade(id, updateDto);
        if (success) {
            return ApiResponse.success("成绩更新成功");
        } else {
            // 在service层，如果找不到gradeId会抛出异常，所以理论上不会走到这里
            return ApiResponse.error("成绩更新失败");
        }
    }
} 