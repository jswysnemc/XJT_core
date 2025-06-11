package com.ljp.xjt.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.dto.TeachingAssignmentDto;
import com.ljp.xjt.dto.TeachingAssignmentRequestDto;
import com.ljp.xjt.entity.Course;
import com.ljp.xjt.entity.TeachingAssignment;
import com.ljp.xjt.service.TeachingAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教学分配管理控制器
 * <p>
 * 提供对教学分配（排课）信息的增删改查接口，仅限管理员访问。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@RestController
@RequestMapping("/admin/teaching-assignments")
@Tag(name = "管理端 - 业务管理", description = "教学分配（排课）管理")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TeachingAssignmentController {

    private final TeachingAssignmentService teachingAssignmentService;

    @Operation(summary = "获取从未排课的课程列表", description = "查询所有在系统中从未被安排过任何教学任务的课程。")
    @GetMapping("/unassigned-courses")
    public ApiResponse<List<Course>> getUnassignedCourses() {
        List<Course> unassignedCourses = teachingAssignmentService.findUnassignedCourses();
        return ApiResponse.success(unassignedCourses);
    }

    @Operation(summary = "分页查询排课列表", description = "获取详细的排课信息列表，支持通过课程名、教师名和班级名进行筛选。")
    @GetMapping
    public ApiResponse<IPage<TeachingAssignmentDto>> listAssignments(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "课程名称 (模糊查询)") @RequestParam(required = false) String courseName,
            @Parameter(description = "教师名称 (模糊查询)") @RequestParam(required = false) String teacherName,
            @Parameter(description = "班级名称 (模糊查询)") @RequestParam(required = false) String className) {
        Page<TeachingAssignmentDto> page = new Page<>(current, size);
        IPage<TeachingAssignmentDto> result = teachingAssignmentService.listAssignments(page, courseName, teacherName, className);
        return ApiResponse.success(result);
    }

    @Operation(summary = "创建新的排课记录", description = "创建一个新的教学分配记录。")
    @PostMapping
    public ApiResponse<TeachingAssignment> createAssignment(@Valid @RequestBody TeachingAssignmentRequestDto requestDto) {
        try {
            TeachingAssignment assignment = teachingAssignmentService.createAssignment(requestDto);
            return ApiResponse.success("排课记录创建成功", assignment);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    @Operation(summary = "更新排课记录", description = "根据ID更新一个已有的教学分配记录。")
    @PutMapping("/{id}")
    public ApiResponse<TeachingAssignment> updateAssignment(
            @Parameter(description = "排课ID") @PathVariable Long id,
            @Valid @RequestBody TeachingAssignmentRequestDto requestDto) {
        try {
            TeachingAssignment assignment = teachingAssignmentService.updateAssignment(id, requestDto);
            return ApiResponse.success("排课记录更新成功", assignment);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    @Operation(summary = "删除排课记录", description = "根据ID删除一个教学分配记录。")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAssignment(@Parameter(description = "排课ID") @PathVariable Long id) {
        try {
            teachingAssignmentService.deleteAssignment(id);
            return ApiResponse.success("排课记录删除成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }
} 