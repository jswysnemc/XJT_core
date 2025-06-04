package com.ljp.xjt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.entity.Classes;
import com.ljp.xjt.entity.CourseSchedule;
import com.ljp.xjt.entity.Teacher;
import com.ljp.xjt.service.ClassesService;
import com.ljp.xjt.service.CourseScheduleService;
import com.ljp.xjt.service.TeacherService;
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

import java.util.List;

/**
 * 教师管理控制器
 * <p>
 * 提供教师管理相关的API接口，包括教师查询、创建、更新等功能
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Slf4j
@RestController
@RequestMapping("/admin/teachers")
@RequiredArgsConstructor
@Validated
@Tag(name = "教师管理", description = "教师管理相关接口")
@PreAuthorize("hasRole('ADMIN')")
public class TeacherController {

    private final TeacherService teacherService;
    private final ClassesService classesService;
    private final CourseScheduleService courseScheduleService;

    /**
     * 分页查询教师列表
     *
     * @param current 当前页码
     * @param size 每页大小
     * @param teacherName 教师姓名（模糊查询）
     * @param departmentId 部门ID
     * @return 教师分页数据
     */
    @GetMapping
    @Operation(summary = "分页查询教师列表", description = "管理员分页查询系统中的所有教师")
    public ApiResponse<IPage<Teacher>> getTeacherList(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") @Positive Long current,
            
            @Parameter(description = "每页大小", example = "10") 
            @RequestParam(defaultValue = "10") @Positive Long size,
            
            @Parameter(description = "教师姓名（模糊查询）")
            @RequestParam(required = false) String teacherName,
            
            @Parameter(description = "部门ID")
            @RequestParam(required = false) Long departmentId) {
        
        log.info("Query teacher list - current: {}, size: {}, teacherName: {}, departmentId: {}", 
                 current, size, teacherName, departmentId);
        
        Page<Teacher> page = new Page<>(current, size);
        IPage<Teacher> teacherPage = teacherService.getTeacherList(page, teacherName, departmentId);
        
        return ApiResponse.success("查询成功", teacherPage);
    }

    /**
     * 根据ID查询教师详情
     *
     * @param id 教师ID
     * @return 教师详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询教师详情", description = "根据教师ID查询教师详细信息")
    public ApiResponse<Teacher> getTeacherById(
            @Parameter(description = "教师ID", required = true)
            @PathVariable @NotNull @Positive Long id) {
        
        log.info("Query teacher by ID: {}", id);
        
        Teacher teacher = teacherService.getTeacherById(id);
        if (teacher == null) {
            return ApiResponse.notFound();
        }
        
        return ApiResponse.success("查询成功", teacher);
    }

    /**
     * 创建新教师
     *
     * @param teacher 教师信息
     * @return 创建结果
     */
    @PostMapping
    @Operation(summary = "创建教师", description = "管理员创建新教师")
    public ApiResponse<Teacher> createTeacher(@Valid @RequestBody Teacher teacher) {
        log.info("Create new teacher: {}", teacher.getTeacherName());
        
        try {
            boolean result = teacherService.createTeacher(teacher);
            if (!result) {
                return ApiResponse.error("教师创建失败");
            }
            
            return ApiResponse.created(teacher);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新教师信息
     *
     * @param id 教师ID
     * @param teacher 教师信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新教师信息", description = "管理员更新教师信息")
    public ApiResponse<Teacher> updateTeacher(
            @Parameter(description = "教师ID", required = true)
            @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody Teacher teacher) {
        
        log.info("Update teacher: {}", id);
        
        teacher.setId(id);
        
        try {
            boolean result = teacherService.updateTeacher(teacher);
            if (!result) {
                return ApiResponse.error("教师更新失败");
            }
            
            Teacher updatedTeacher = teacherService.getById(id);
            return ApiResponse.success("更新成功", updatedTeacher);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除教师
     *
     * @param id 教师ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除教师", description = "管理员删除指定教师（逻辑删除）")
    public ApiResponse<Void> deleteTeacher(
            @Parameter(description = "教师ID", required = true)
            @PathVariable @NotNull @Positive Long id) {
        
        log.info("Delete teacher: {}", id);
        
        // 先查询是否存在
        Teacher teacher = teacherService.getById(id);
        if (teacher == null) {
            return ApiResponse.notFound();
        }
        
        // 检查是否有关联数据，如班级班主任、教学任务等
        // 1. 检查是否是班级班主任
        LambdaQueryWrapper<Classes> classQuery = new LambdaQueryWrapper<>();
        classQuery.eq(Classes::getAdvisorTeacherId, id);
        long classCount = classesService.count(classQuery);
        
        if (classCount > 0) {
            log.warn("Cannot delete teacher with ID {} because they are the advisor for {} classes", id, classCount);
            return ApiResponse.error(400, "该教师是" + classCount + "个班级的班主任，不能直接删除。请先更换这些班级的班主任");
        }
        
        // 2. 检查是否有教学任务安排
        boolean hasSchedules = courseScheduleService.hasSchedulesByTeacherId(id);
        if (hasSchedules) {
            log.warn("Cannot delete teacher with ID {} because they have teaching schedules", id);
            return ApiResponse.error(400, "该教师有关联的课程安排记录，不能直接删除。请先删除相关教学任务");
        }
        
        boolean result = teacherService.removeById(id);
        if (!result) {
            return ApiResponse.error("教师删除失败");
        }
        
        return ApiResponse.success("教师删除成功", null);
    }

    /**
     * 检查教工号是否存在
     *
     * @param teacherNumber 教工号
     * @return 是否存在
     */
    @GetMapping("/check-teacher-number")
    @Operation(summary = "检查教工号", description = "检查教工号是否已存在")
    public ApiResponse<Boolean> checkTeacherNumber(
            @Parameter(description = "教工号", required = true)
            @RequestParam String teacherNumber) {
        
        log.info("Check teacher number: {}", teacherNumber);
        
        boolean exists = teacherService.checkTeacherNumberExists(teacherNumber);
        return ApiResponse.success("查询成功", exists);
    }

    /**
     * 获取所有教师列表(用于下拉选择)
     *
     * @return 教师列表
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有教师", description = "获取所有教师列表，用于下拉选择")
    public ApiResponse<List<Teacher>> getAllTeachers() {
        log.info("Get all teachers for selection");
        
        List<Teacher> teachers = teacherService.getAllTeachers();
        return ApiResponse.success("查询成功", teachers);
    }

    /**
     * 根据用户ID查询教师信息
     *
     * @param userId 用户ID
     * @return 教师信息
     */
    @GetMapping("/by-user/{userId}")
    @Operation(summary = "根据用户ID查询教师", description = "根据用户ID查询对应的教师信息")
    public ApiResponse<Teacher> getTeacherByUserId(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId) {
        
        log.info("Query teacher by user ID: {}", userId);
        
        Teacher teacher = teacherService.getTeacherByUserId(userId);
        if (teacher == null) {
            return ApiResponse.notFound();
        }
        
        return ApiResponse.success("查询成功", teacher);
    }
} 