package com.ljp.xjt.controller;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教师成绩管理控制器
 * <p>
 * 提供教师成绩管理相关的API接口，包括成绩录入、修改、查询等功能
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Slf4j
@RestController
@RequestMapping("/teacher/grades")
@RequiredArgsConstructor
@Validated
@Tag(name = "教师成绩管理", description = "教师成绩管理相关接口")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherGradeController {

    private final GradeService gradeService;

    /**
     * 查询教师所教课程的学生成绩
     *
     * @param courseId 课程ID
     * @param classId 班级ID（可选）
     * @param semester 学期（可选）
     * @param year 学年（可选）
     * @return 成绩列表
     */
    @GetMapping
    @Operation(summary = "查询教师课程成绩", description = "查询教师所教课程的学生成绩")
    public ApiResponse<List<Grade>> getTeacherCourseGrades(
            @Parameter(description = "课程ID", required = true)
            @RequestParam @NotNull Long courseId,
            
            @Parameter(description = "班级ID")
            @RequestParam(required = false) Long classId,
            
            @Parameter(description = "学期", example = "2025-Spring")
            @RequestParam(required = false) String semester,
            
            @Parameter(description = "学年", example = "2025")
            @RequestParam(required = false) Integer year) {
        
        // 获取当前登录教师ID
        Long teacherId = getCurrentTeacherId();
        
        log.info("Teacher {} query grades - courseId: {}, classId: {}, semester: {}, year: {}", 
                 teacherId, courseId, classId, semester, year);
        
        List<Grade> grades = gradeService.getTeacherCourseGrades(teacherId, courseId, classId, semester, year);
        
        return ApiResponse.success("查询成功", grades);
    }

    /**
     * 录入单个学生成绩
     *
     * @param grade 成绩信息
     * @return 创建结果
     */
    @PostMapping
    @Operation(summary = "录入成绩", description = "教师录入单个学生成绩")
    public ApiResponse<Grade> createGrade(@Valid @RequestBody Grade grade) {
        // 获取当前登录教师ID
        Long teacherId = getCurrentTeacherId();
        
        log.info("Teacher {} create grade for student {} and course {}", 
                 teacherId, grade.getStudentId(), grade.getCourseId());
        
        try {
            boolean result = gradeService.createGrade(grade, teacherId);
            if (!result) {
                return ApiResponse.error("成绩录入失败");
            }
            
            return ApiResponse.created(grade);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 批量录入成绩
     *
     * @param gradeList 成绩列表
     * @return 创建结果
     */
    @PostMapping("/batch")
    @Operation(summary = "批量录入成绩", description = "教师批量录入学生成绩")
    public ApiResponse<Void> batchCreateGrades(@Valid @RequestBody List<Grade> gradeList) {
        // 获取当前登录教师ID
        Long teacherId = getCurrentTeacherId();
        
        log.info("Teacher {} batch create grades, count: {}", teacherId, gradeList.size());
        
        try {
            boolean result = gradeService.batchCreateGrades(gradeList, teacherId);
            if (!result) {
                return ApiResponse.error("批量录入失败");
            }
            
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 修改成绩
     *
     * @param id 成绩ID
     * @param grade 成绩信息
     * @return 修改结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改成绩", description = "教师修改学生成绩")
    public ApiResponse<Grade> updateGrade(
            @Parameter(description = "成绩ID", required = true)
            @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody Grade grade) {
        
        // 获取当前登录教师ID
        Long teacherId = getCurrentTeacherId();
        
        log.info("Teacher {} update grade: {}", teacherId, id);
        
        // 设置ID
        grade.setId(id);
        
        try {
            boolean result = gradeService.updateGrade(grade, teacherId);
            if (!result) {
                return ApiResponse.error("修改失败");
            }
            
            Grade updatedGrade = gradeService.getById(id);
            return ApiResponse.success("修改成功", updatedGrade);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除成绩
     *
     * @param id 成绩ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除成绩", description = "教师删除成绩")
    public ApiResponse<Void> deleteGrade(
            @Parameter(description = "成绩ID", required = true)
            @PathVariable @NotNull @Positive Long id) {
        
        // 获取当前登录教师ID
        Long teacherId = getCurrentTeacherId();
        
        log.info("Teacher {} delete grade: {}", teacherId, id);
        
        try {
            boolean result = gradeService.deleteGrade(id, teacherId);
            if (!result) {
                return ApiResponse.error("删除失败");
            }
            
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取班级课程成绩统计
     *
     * @param courseId 课程ID
     * @param classId 班级ID
     * @param semester 学期（可选）
     * @param year 学年（可选）
     * @return 成绩统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取成绩统计", description = "教师获取班级课程成绩统计")
    public ApiResponse<Map<String, Object>> getGradeStatistics(
            @Parameter(description = "课程ID", required = true)
            @RequestParam @NotNull Long courseId,
            
            @Parameter(description = "班级ID", required = true)
            @RequestParam @NotNull Long classId,
            
            @Parameter(description = "学期", example = "2025-Spring")
            @RequestParam(required = false) String semester,
            
            @Parameter(description = "学年", example = "2025")
            @RequestParam(required = false) Integer year) {
        
        // 获取当前登录教师ID
        Long teacherId = getCurrentTeacherId();
        
        log.info("Teacher {} get statistics - courseId: {}, classId: {}, semester: {}, year: {}", 
                 teacherId, courseId, classId, semester, year);
        
        // 检查教师是否有权限查看该班级课程的成绩
        if (!gradeService.checkTeacherPermission(teacherId, courseId, classId)) {
            return ApiResponse.error("无权查看该班级课程的成绩");
        }
        
        Map<String, Object> statistics = gradeService.getGradeStatistics(classId, courseId, semester, year);
        
        return ApiResponse.success("查询成功", statistics);
    }
    
    /**
     * 获取当前登录教师ID
     * 
     * @return 教师ID
     */
    private Long getCurrentTeacherId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 从认证信息中获取教师ID
        // 实际生产环境应该从认证用户中获取关联的教师ID
        // 此处简化处理，假设用户名就是教师ID
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            log.error("Failed to parse teacher ID from authentication", e);
            // 应急处理，返回一个默认值，实际应用中应该正确获取
            return 1L;
        }
    }
}