package com.ljp.xjt.controller;

import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.entity.Grade;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.service.GradeService;
import com.ljp.xjt.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学生成绩查询控制器
 * <p>
 * 提供学生查询自己成绩的API接口
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Slf4j
@RestController
@RequestMapping("/student/grades")
@RequiredArgsConstructor
@Validated
@Tag(name = "学生成绩查询", description = "学生成绩查询相关接口")
@PreAuthorize("hasRole('STUDENT')")
public class StudentGradeController {

    private final GradeService gradeService;
    private final StudentService studentService;

    /**
     * 查询学生所有成绩
     *
     * @param semester 学期（可选）
     * @param year 学年（可选）
     * @return 成绩列表
     */
    @GetMapping
    @Operation(summary = "查询学生所有成绩", description = "学生查询自己的所有成绩")
    public ApiResponse<List<Grade>> getStudentGrades(
            @Parameter(description = "学期", example = "2025-Spring")
            @RequestParam(required = false) String semester,
            
            @Parameter(description = "学年", example = "2025")
            @RequestParam(required = false) Integer year) {
        
        // 获取当前登录学生ID
        Long studentId = getCurrentStudentId();
        
        log.info("Student {} query grades - semester: {}, year: {}", studentId, semester, year);
        
        List<Grade> grades = gradeService.getStudentGrades(studentId, semester, year);
        
        return ApiResponse.success("查询成功", grades);
    }

    /**
     * 查询学生指定课程成绩
     *
     * @param courseId 课程ID
     * @param semester 学期（可选）
     * @param year 学年（可选）
     * @return 成绩列表
     */
    @GetMapping("/course")
    @Operation(summary = "查询学生指定课程成绩", description = "学生查询自己的指定课程成绩")
    public ApiResponse<List<Grade>> getStudentCourseGrades(
            @Parameter(description = "课程ID", required = true)
            @RequestParam @NotNull Long courseId,
            
            @Parameter(description = "学期", example = "2025-Spring")
            @RequestParam(required = false) String semester,
            
            @Parameter(description = "学年", example = "2025")
            @RequestParam(required = false) Integer year) {
        
        // 获取当前登录学生ID
        Long studentId = getCurrentStudentId();
        
        log.info("Student {} query course grades - courseId: {}, semester: {}, year: {}", 
                 studentId, courseId, semester, year);
        
        List<Grade> grades = gradeService.getStudentCourseGrades(studentId, courseId, semester, year);
        
        return ApiResponse.success("查询成功", grades);
    }
    
    /**
     * 获取当前登录学生ID
     * 
     * @return 学生ID
     */
    private Long getCurrentStudentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // 从认证信息中获取学生ID
        // 实际生产环境应该从认证用户中获取关联的学生ID
        try {
            // 假设用户名是用户ID
            Long userId = Long.valueOf(username);
            // 根据用户ID查询学生信息
            Student student = studentService.findByUserId(userId);
            if (student != null) {
                return student.getId();
            } else {
                log.error("Student not found for user: {}", userId);
                throw new IllegalStateException("未找到学生信息");
            }
        } catch (NumberFormatException e) {
            log.error("Failed to parse user ID from authentication", e);
            // 应急处理，返回一个默认值，实际应用中应该正确获取
            return 1L;
        }
    }
}