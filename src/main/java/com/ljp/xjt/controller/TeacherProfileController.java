package com.ljp.xjt.controller;

import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.dto.TeacherProfileDto;
import com.ljp.xjt.dto.TeacherProfileUpdateRequestDto;
import com.ljp.xjt.dto.TeachingStatisticsDto;
import com.ljp.xjt.entity.Teacher;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.service.StudentService;
import com.ljp.xjt.service.TeacherService;
import com.ljp.xjt.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


/**
 * 教师个人资料控制器
 * <p>
 * 提供教师查看和管理个人资料的API接口
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Slf4j
@RestController
@RequestMapping("/teacher/profile")
@RequiredArgsConstructor
@Tag(name = "教师个人资料", description = "教师个人资料相关接口")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherProfileController {

    private final TeacherService teacherService;
    private final UserService userService;
    private final StudentService studentService;

    /**
     * 获取当前登录教师的个人资料
     *
     * @return 教师个人资料
     */
    @GetMapping("/me")
    @Operation(summary = "获取个人资料", description = "获取当前登录教师的个人资料")
    public ApiResponse<TeacherProfileDto> getMyProfile() {
        // 获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.info("Get teacher profile for: {}", username);

        // 根据用户名获取用户ID
        User user = userService.findByUsername(username);
        if (user == null) {
            log.warn("User not found with username: {}", username);
            return ApiResponse.error(404, "用户不存在");
        }

        TeacherProfileDto teacherProfile = teacherService.getTeacherProfileByUserId(user.getId());
        if (teacherProfile == null) {
            log.warn("Teacher profile not found for user ID: {}", user.getId());
            return ApiResponse.notFound();
        }

        return ApiResponse.success("查询成功", teacherProfile);
    }

    /**
     * 更新教师个人资料
     *
     * @param updateDto 教师资料更新请求
     * @return 更新结果
     */
    @PutMapping("/me")
    @Operation(summary = "更新个人资料", description = "更新当前登录教师的个人资料。可修改的字段包括：教师姓名、电子邮箱、手机号码。")
    public ApiResponse<Void> updateMyProfile(@Valid @RequestBody TeacherProfileUpdateRequestDto updateDto) {
        // 获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.info("Update teacher profile for: {} with data: {}", username, updateDto);

        // 根据用户名获取用户ID
        User user = userService.findByUsername(username);
        if (user == null) {
            log.warn("User not found with username: {}", username);
            return ApiResponse.error(404, "用户不存在");
        }

        boolean success = teacherService.updateTeacherProfile(user.getId(), updateDto);

        if (success) {
            return ApiResponse.success("个人资料更新成功");
        } else {
            return ApiResponse.error("个人资料更新失败");
        }
    }

    /**
     * 获取教学统计信息
     *
     * @return 教学统计信息
     */
    @GetMapping("/teaching-stats")
    @Operation(summary = "获取教学统计", description = "获取当前教师的教学统计信息")
    public ApiResponse<TeachingStatisticsDto> getTeachingStats() {
        // 获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.info("Get teaching stats for teacher: {}", username);

        // 1. 根据用户名获取用户ID和教师信息
        User user = userService.findByUsername(username);
        if (user == null) {
            log.warn("User not found for username: {}", username);
            return ApiResponse.error(404, "用户不存在");
        }

        Teacher teacher = teacherService.getTeacherByUserId(user.getId());
        if (teacher == null) {
            log.warn("Teacher not found for user ID: {}", user.getId());
            return ApiResponse.notFound();
        }

        // 2. 调用服务获取统计数据
        TeachingStatisticsDto stats = teacherService.getTeachingStatistics(teacher.getId());

        return ApiResponse.success("查询成功", stats);
    }
} 