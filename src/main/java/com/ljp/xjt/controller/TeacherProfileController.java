package com.ljp.xjt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.entity.Teacher;
import com.ljp.xjt.entity.TeacherCourse;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.service.CourseScheduleService;
import com.ljp.xjt.service.StudentService;
import com.ljp.xjt.service.TeacherCourseService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final TeacherCourseService teacherCourseService;
    private final StudentService studentService;
    private final CourseScheduleService courseScheduleService;

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
        
        // 根据用户名获取用户ID，然后获取教师信息
        User user = userService.findByUsername(username);
        if (user == null) {
            log.warn("User not found with username: {}", username);
            return ApiResponse.error(404, "用户不存在");
        }
        
        Long userId = user.getId();
        Teacher teacher = teacherService.getTeacherByUserId(userId);
        if (teacher == null) {
            log.warn("Teacher not found for user ID: {}", userId);
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
        
        // 根据用户名获取用户ID和教师ID
        User user = userService.findByUsername(username);
        if (user == null) {
            log.warn("User not found with username: {}", username);
            return ApiResponse.error(404, "用户不存在");
        }
        
        Long userId = user.getId();
        Teacher existingTeacher = teacherService.getTeacherByUserId(userId);
        if (existingTeacher == null) {
            log.warn("Teacher not found for user ID: {}", userId);
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
    public ApiResponse<Map<String, Object>> getTeachingStats() {
        // 获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        log.info("Get teaching stats for teacher: {}", username);
        
        // 实现教学统计信息查询逻辑
        // 包括：教授班级数、教授课程数、学生数量等
        
        // 获取教师信息
        User user = userService.findByUsername(username);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        
        Long userId = user.getId();
        Teacher teacher = teacherService.getTeacherByUserId(userId);
        if (teacher == null) {
            return ApiResponse.notFound();
        }
        
        Long teacherId = teacher.getId();
        
        // 查询该教师的课程安排
        LambdaQueryWrapper<TeacherCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherCourse::getTeacherId, teacherId);
        List<TeacherCourse> teacherCourses = teacherCourseService.list(queryWrapper);
        
        // 统计数据
        // 1. 教授的班级数 (不重复的班级)
        Set<Long> classIds = teacherCourses.stream()
            .map(TeacherCourse::getClassId)
            .filter(id -> id != null)
            .collect(Collectors.toSet());
        int classCount = classIds.size();
        
        // 2. 教授的课程数 (不重复的课程)
        Set<Long> courseIds = teacherCourses.stream()
            .map(TeacherCourse::getCourseId)
            .filter(id -> id != null)
            .collect(Collectors.toSet());
        int courseCount = courseIds.size();
        
        // 3. 涉及的学生总数 (属于这些班级的所有学生)
        int studentCount = 0;
        if (!classIds.isEmpty()) {
            LambdaQueryWrapper<Student> studentQuery = new LambdaQueryWrapper<>();
            studentQuery.in(Student::getClassId, classIds);
            studentCount = (int) studentService.count(studentQuery);
        }
        
        // 4. 课程安排总数
        int scheduleCount = teacherCourses.size();
        
        // 组装统计数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("teacherId", teacherId);
        stats.put("teacherName", teacher.getTeacherName());
        stats.put("classCount", classCount);
        stats.put("courseCount", courseCount);
        stats.put("studentCount", studentCount);
        stats.put("scheduleCount", scheduleCount);
        
        return ApiResponse.success("查询成功", stats);
    }
} 