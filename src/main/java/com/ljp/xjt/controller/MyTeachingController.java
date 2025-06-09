package com.ljp.xjt.controller;

import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.dto.BatchGradeEntryDto;
import com.ljp.xjt.dto.BatchGradeResponseDto;
import com.ljp.xjt.dto.GradeUpdateRequest;
import com.ljp.xjt.dto.ScoreUpdateRequest;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.dto.TeacherClassDto;
import com.ljp.xjt.dto.TeacherCourseDto;
import com.ljp.xjt.dto.StudentDto;
import com.ljp.xjt.security.SecurityUser;
import com.ljp.xjt.service.TeacherService;
import com.ljp.xjt.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ljp
 * @version 1.0
 * @since 2025-06-09
 */
@Slf4j
@RestController
@RequestMapping("/teacher")
@Tag(name = "教师 - 我的教学", description = "提供教师查询课程、班级、管理成绩等相关接口")
@RequiredArgsConstructor
public class MyTeachingController {
    private final TeacherService teacherService;
    private final UserService userService;

    /**
     * 获取当前登录的用户名
     *
     * @return 当前登录的用户名
     * @throws IllegalStateException 如果无法获取用户信息
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("无法获取当前用户信息，请重新登录");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        throw new IllegalStateException("无法识别的用户信息类型");
    }

    /**
     * 获取当前教师教授的所有课程
     *
     * @return 课程列表
     */
    @GetMapping("/courses")
    @Operation(summary = "获取当前教师教授的所有课程")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<TeacherCourseDto>> getMyCourses() {
        String username = getCurrentUsername();
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new IllegalStateException("用户不存在");
        }
        List<TeacherCourseDto> courses = teacherService.findCoursesByUserId(user.getId());
        return ApiResponse.success(courses);
    }

    /**
     * 获取指定课程下，当前教师教授的所有班级
     *
     * @param courseId 课程ID
     * @return 班级列表
     */
    @GetMapping("/courses/{courseId}/classes")
    @Operation(summary = "获取指定课程下的班级列表")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<TeacherClassDto>> getMyClassesForCourse(
            @Parameter(description = "课程ID") @PathVariable("courseId") Long courseId) {
        String username = getCurrentUsername();
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new IllegalStateException("用户不存在");
        }
        List<TeacherClassDto> classes = teacherService.findClassesByCourseId(user.getId(), courseId);
        return ApiResponse.success(classes);
    }

    /**
     * 获取指定课程和班级的学生名册（包含成绩）
     *
     * @param courseId 课程ID
     * @param classId  班级ID
     * @return 学生名册列表
     */
    @GetMapping("/courses/{courseId}/classes/{classId}/roster")
    @Operation(summary = "获取指定班级的学生名册（含成绩）")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<StudentDto>> getClassRoster(
            @Parameter(description = "课程ID") @PathVariable("courseId") Long courseId,
            @Parameter(description = "班级ID") @PathVariable("classId") Long classId) {
        String username = getCurrentUsername();
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new IllegalStateException("用户不存在");
        }
        List<StudentDto> students = teacherService.findStudentsByClassAndCourse(user.getId(), classId, courseId);
        return ApiResponse.success(students);
    }

    /**
     * 修改或录入学生成绩
     *
     * @param courseId 课程ID
     * @param classId  班级ID
     * @param studentId 学生ID
     * @param request 包含分数的请求体
     * @return 操作结果
     */
    @PutMapping("/courses/{courseId}/classes/{classId}/students/{studentId}/grade")
    @Operation(summary = "修改或录入单个学生成绩", description = "为指定班级的指定学生录入或修改一门课程的成绩。")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<Void> updateStudentGrade(
            @Parameter(description = "课程ID") @PathVariable("courseId") Long courseId,
            @Parameter(description = "班级ID") @PathVariable("classId") Long classId,
            @Parameter(description = "学生ID") @PathVariable("studentId") Long studentId,
            @Valid @RequestBody ScoreUpdateRequest request) {
        String username = getCurrentUsername();
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new IllegalStateException("用户不存在");
        }
        
        try {
            boolean success = teacherService.updateGrade(user.getId(), courseId, classId, studentId, request.getScore());
            if (success) {
                return ApiResponse.success("成绩更新成功");
            } else {
                return ApiResponse.error("成绩更新失败");
            }
        } catch (SecurityException e) {
            return ApiResponse.forbidden(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    @PostMapping("/courses/{courseId}/classes/{classId}/grades/batch")
    @Operation(summary = "批量导入成绩", description = "通过JSON数据批量为指定班级的学生录入或更新某门课程的成绩。")
    public ApiResponse<BatchGradeResponseDto> batchUpdateGrades(
            @PathVariable("courseId") Long courseId,
            @PathVariable("classId") Long classId,
            @Valid @RequestBody List<BatchGradeEntryDto> gradeEntries) {

        // 1. 从SecurityContext中获取封装好的User实体
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((SecurityUser) authentication.getPrincipal()).getUser();
        Long userId = currentUser.getId();

        log.info("Batch grade update request for courseId: {}, classId: {} by user: {}", courseId, classId, userId);

        // 2. 调用服务层处理批量更新
        BatchGradeResponseDto responseDto = teacherService.batchUpdateGrades(userId, courseId, classId, gradeEntries);

        // 3. 构建响应消息
        String message = String.format("批量导入完成。成功 %d 条，失败 %d 条。",
                responseDto.getSuccessCount(), responseDto.getFailureCount());

        return ApiResponse.success(message, responseDto);
    }
} 