package com.ljp.xjt.controller;

import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.dto.StudentCourseDTO;
import com.ljp.xjt.dto.StudentGradeDTO;
import com.ljp.xjt.dto.StudentProfileDTO;
import com.ljp.xjt.dto.StudentProfileUpdateDTO;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.security.SecurityUser;
import com.ljp.xjt.service.StudentService;
import com.ljp.xjt.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学生角色功能控制器
 * <p>
 * 提供学生角色专属的API接口，例如查询个人信息、成绩等。
 * 所有接口都需要学生角色权限。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
@Tag(name = "学生端 - 个人业务接口")
@Slf4j
public class StudentRoleController {

    private final StudentService studentService;
    private final UserService userService;

    /**
     * [学生] 获取当前登录学生的个人信息
     *
     * @return ApiResponse<StudentProfileDTO> 当前登录学生的详细个人信息
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "获取我的个人信息", description = "查询当前登录学生的详细信息，包含用户和学生资料。需要学生权限。")
    public ApiResponse<StudentProfileDTO> getCurrentStudentInfo() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof SecurityUser securityUser) {
            User user = securityUser.getUser();
            Student student = studentService.findByUserId(user.getId());
            
            if (student != null) {
                // 1. 创建 DTO
                StudentProfileDTO profileDTO = new StudentProfileDTO();
                
                // 2. 填充 User 信息
                profileDTO.setUsername(user.getUsername());
                profileDTO.setEmail(user.getEmail());
                profileDTO.setPhone(user.getPhone());
                
                // 3. 填充 Student 信息
                profileDTO.setId(student.getId());
                profileDTO.setUserId(student.getUserId());
                profileDTO.setStudentNumber(student.getStudentNumber());
                profileDTO.setStudentName(student.getStudentName());
                profileDTO.setGender(student.getGender());
                profileDTO.setBirthDate(student.getBirthDate());
                profileDTO.setClassId(student.getClassId());
                profileDTO.setCreatedTime(student.getCreatedTime());
                profileDTO.setUpdatedTime(student.getUpdatedTime());

                return ApiResponse.success(profileDTO);
            }
        }
        return ApiResponse.error(404, "未找到当前用户的学生信息");
    }

    /**
     * [学生] 更新当前登录学生的个人信息
     *
     * @param updateDTO 包含待更新信息的DTO
     * @return ApiResponse<Void>
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "更新我的个人信息", description = "更新当前登录学生的部分个人信息，如邮箱、电话、姓名、性别。需要学生权限。")
    public ApiResponse<Void> updateMyProfile(@Valid @RequestBody StudentProfileUpdateDTO updateDTO) {
        try {
            boolean success = studentService.updateMyProfile(updateDTO);
            if (success) {
                return ApiResponse.success("个人信息更新成功");
            }
            // 通常, 如果 success 为 false, 应该有一个更具体的原因, 但当前实现总是返回 true 或抛出异常
            return ApiResponse.error(500, "个人信息更新失败，但未报告具体异常。");
        } catch (Exception e) {
            log.error("更新学生个人信息时发生异常", e);
            return ApiResponse.error(500, "个人信息更新失败: " + e.getMessage());
        }
    }

    /**
     * 查询当前登录学生的所有成绩
     * <p>
     * 此接口供学生角色调用，用于查询自己的全部课程成绩。
     * 返回数据包含学期、课程、教师、分数等详细信息。
     * </p>
     *
     * @return ApiResponse<List<StudentGradeDTO>> 包含学生成绩列表的API响应
     */
    @GetMapping("/grades")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "查询我的成绩", description = "获取当前登录学生的所有科目成绩详情")
    public ApiResponse<List<StudentGradeDTO>> getMyGrades() {
        List<StudentGradeDTO> grades = studentService.findMyGrades();
        return ApiResponse.success(grades);
    }

    /**
     * 查询当前登录学生的所有课程信息
     * <p>
     * 此接口供学生角色调用，用于查询自己的全部课程安排。
     * 返回数据包含课程、教师、学期等详细信息，不包含成绩。
     * </p>
     *
     * @return ApiResponse<List<StudentCourseDTO>> 包含学生课程列表的API响应
     */
    @GetMapping("/courses")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "查询我的课程", description = "获取当前登录学生的所有课程安排详情")
    public ApiResponse<List<StudentCourseDTO>> getMyCourses() {
        List<StudentCourseDTO> courses = studentService.findMyCourses();
        return ApiResponse.success(courses);
    }
} 