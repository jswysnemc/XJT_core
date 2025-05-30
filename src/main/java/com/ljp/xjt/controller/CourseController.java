package com.ljp.xjt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.entity.Course;
import com.ljp.xjt.entity.Grade;
import com.ljp.xjt.entity.CourseSchedule;
import com.ljp.xjt.service.CourseService;
import com.ljp.xjt.service.GradeService;
import com.ljp.xjt.service.CourseScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 课程管理控制器
 * <p>
 * 提供课程信息的RESTful API接口，包括增、删、改、查等操作。
 * 所有接口均需要管理员权限。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@RestController
@RequestMapping("/api/admin/courses")
@Tag(name = "课程管理", description = "提供课程信息的增删改查接口")
@PreAuthorize("hasRole('ADMIN')")
public class CourseController {

    private static final Logger log = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;
    private final GradeService gradeService;
    private final CourseScheduleService courseScheduleService;

    @Autowired
    public CourseController(CourseService courseService, 
                          GradeService gradeService,
                          CourseScheduleService courseScheduleService) {
        this.courseService = courseService;
        this.gradeService = gradeService;
        this.courseScheduleService = courseScheduleService;
    }

    /**
     * 创建新课程
     *
     * @param course 课程信息实体
     * @return ApiResponse<Course> 创建结果及创建后的课程信息
     */
    @PostMapping
    @Operation(summary = "创建新课程", description = "创建一个新的课程信息")
    public ApiResponse<Course> createCourse(@Valid @RequestBody Course course) {
        // 1. 校验课程名称和编码是否已存在
        if (courseService.checkCourseNameExists(course.getCourseName(), null)) {
            return ApiResponse.error(400, "课程名称已存在");
        }
        if (courseService.checkCourseCodeExists(course.getCourseCode(), null)) {
            return ApiResponse.error(400, "课程编码已存在");
        }
        // 2. 保存课程信息
        boolean success = courseService.save(course);
        if (success) {
            log.info("Course created successfully: {}", course.getCourseName());
            return ApiResponse.created(course);
        }
        return ApiResponse.error(500, "课程创建失败");
    }

    /**
     * 根据ID获取课程信息
     *
     * @param id 课程ID
     * @return ApiResponse<Course> 课程信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取课程详情", description = "根据ID查询课程详细信息")
    public ApiResponse<Course> getCourseById(@Parameter(description = "课程ID") @PathVariable Long id) {
        Course course = courseService.getById(id);
        if (course != null) {
            return ApiResponse.success(course);
        }
        return ApiResponse.notFound();
    }

    /**
     * 更新课程信息
     *
     * @param id     课程ID
     * @param course 更新后的课程信息实体
     * @return ApiResponse<Course> 更新结果及更新后的课程信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新课程信息", description = "根据ID更新指定课程的信息")
    public ApiResponse<Course> updateCourse(@Parameter(description = "课程ID") @PathVariable Long id,
                                          @Valid @RequestBody Course course) {
        // 1. 检查课程是否存在
        Course existingCourse = courseService.getById(id);
        if (existingCourse == null) {
            return ApiResponse.notFound();
        }
        // 2. 校验更新后的课程名称和编码是否与其它课程冲突
        if (courseService.checkCourseNameExists(course.getCourseName(), id)) {
            return ApiResponse.error(400, "课程名称已存在");
        }
        if (courseService.checkCourseCodeExists(course.getCourseCode(), id)) {
            return ApiResponse.error(400, "课程编码已存在");
        }
        // 3. 设置ID并更新
        course.setId(id);
        boolean success = courseService.updateById(course);
        if (success) {
            log.info("Course updated successfully: {}", course.getCourseName());
            return ApiResponse.success("课程更新成功", courseService.getById(id));
        }
        return ApiResponse.error(500, "课程更新失败");
    }

    /**
     * 根据ID删除课程
     *
     * @param id 课程ID
     * @return ApiResponse<Void> 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程", description = "根据ID删除指定课程")
    public ApiResponse<Void> deleteCourse(@Parameter(description = "课程ID") @PathVariable Long id) {
        Course existingCourse = courseService.getById(id);
        if (existingCourse == null) {
            return ApiResponse.notFound();
        }

        // 删除课程前检查是否有班级课程安排、成绩等关联数据
        // 1. 检查是否有课程安排关联
        boolean hasSchedules = courseScheduleService.hasSchedulesByCourseId(id);
        if (hasSchedules) {
            log.warn("Cannot delete course with ID {} because it has associated schedules", id);
            return ApiResponse.error(400, "该课程已有排课记录，不能直接删除。请先删除相关课程安排");
        }
        
        // 2. 检查是否有成绩记录关联
        LambdaQueryWrapper<Grade> gradeQuery = new LambdaQueryWrapper<>();
        gradeQuery.eq(Grade::getCourseId, id);
        long gradeCount = gradeService.count(gradeQuery);
        
        if (gradeCount > 0) {
            log.warn("Cannot delete course with ID {} because it has {} grade records", id, gradeCount);
            return ApiResponse.error(400, "该课程有关联的成绩记录，不能直接删除。请先删除相关成绩记录");
        }
        
        // 执行删除操作
        boolean success = courseService.removeById(id);
        if (success) {
            log.info("Course deleted successfully: ID={}, Name={}", id, existingCourse.getCourseName());
            return ApiResponse.success("课程删除成功", null);
        }
        return ApiResponse.error(500, "课程删除失败");
    }

    /**
     * 分页查询课程列表
     *
     * @param pageNum    当前页码
     * @param pageSize   每页数量
     * @param courseName 课程名称 (可选查询条件)
     * @param courseCode 课程编码 (可选查询条件)
     * @return ApiResponse<Page<Course>> 分页后的课程列表
     */
    @GetMapping
    @Operation(summary = "分页查询课程列表", description = "可根据课程名称、编码进行筛选")
    public ApiResponse<Page<Course>> listCourses(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "课程名称查询") @RequestParam(required = false) String courseName,
            @Parameter(description = "课程编码查询") @RequestParam(required = false) String courseCode) {

        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(courseName), Course::getCourseName, courseName)
                    .like(StringUtils.hasText(courseCode), Course::getCourseCode, courseCode)
                    .orderByDesc(Course::getCreatedTime); // 默认按创建时间降序

        Page<Course> page = new Page<>(pageNum, pageSize);
        courseService.page(page, queryWrapper);
        return ApiResponse.success(page);
    }

    /**
     * 获取所有课程列表 (不分页)
     *
     * @return ApiResponse<List<Course>> 所有课程列表
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有课程列表", description = "查询所有课程信息，不进行分页")
    public ApiResponse<List<Course>> listAllCourses() {
        List<Course> list = courseService.list(new LambdaQueryWrapper<Course>().orderByAsc(Course::getCourseName));
        return ApiResponse.success(list);
    }
} 