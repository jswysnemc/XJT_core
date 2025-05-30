package com.ljp.xjt.controller;

import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.entity.Teacher;
import com.ljp.xjt.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 获取当前登录教师的个人资料
     *
     * @return 教师个人资料
     */
    @GetMapping("/me")
    @Operation(summary = "获取个人资料", description = "获取当前登录教师的个人资料")
    public ApiResponse<Teacher> getMyProfile() {
        // 获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        log.info("Get teacher profile for: {}", username);
        
        // TODO: 根据用户名获取用户ID，然后获取教师信息
        // 这里需要通过UserService先获取userId
        // 暂时使用1作为示例
        Long userId = 1L; // 这里应该是实际逻辑
        
        Teacher teacher = teacherService.getTeacherByUserId(userId);
        if (teacher == null) {
            return ApiResponse.notFound();
        }
        
        return ApiResponse.success("查询成功", teacher);
    }

    /**
     * 更新教师个人资料
     *
     * @param teacher 教师资料
     * @return 更新结果
     */
    @PutMapping("/me")
    @Operation(summary = "更新个人资料", description = "更新当前登录教师的个人资料")
    public ApiResponse<Teacher> updateMyProfile(@RequestBody Teacher teacher) {
        // 获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        log.info("Update teacher profile for: {}", username);
        
        // TODO: 根据用户名获取用户ID和教师ID
        // 暂时使用1作为示例
        Long userId = 1L; // 这里应该是实际逻辑
        
        Teacher existingTeacher = teacherService.getTeacherByUserId(userId);
        if (existingTeacher == null) {
            return ApiResponse.notFound();
        }
        
        // 确保只能更新自己的资料
        teacher.setId(existingTeacher.getId());
        teacher.setUserId(existingTeacher.getUserId());
        
        // 限制可以修改的字段，例如不允许修改教工号
        teacher.setTeacherNumber(existingTeacher.getTeacherNumber());
        
        try {
            boolean result = teacherService.updateTeacher(teacher);
            if (!result) {
                return ApiResponse.error("个人资料更新失败");
            }
            
            Teacher updatedTeacher = teacherService.getById(existingTeacher.getId());
            return ApiResponse.success("更新成功", updatedTeacher);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取教学统计信息
     *
     * @return 教学统计信息
     */
    @GetMapping("/teaching-stats")
    @Operation(summary = "获取教学统计", description = "获取当前教师的教学统计信息")
    public ApiResponse<Object> getTeachingStats() {
        // 获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        log.info("Get teaching stats for teacher: {}", username);
        
        // TODO: 实现教学统计信息查询逻辑
        // 包括：教授班级数、教授课程数、学生数量等
        
        // 暂时返回空对象
        return ApiResponse.success("查询成功", new Object());
    }
} 