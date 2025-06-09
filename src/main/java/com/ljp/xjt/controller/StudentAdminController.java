package com.ljp.xjt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.entity.Grade;
import com.ljp.xjt.service.StudentService;
import com.ljp.xjt.service.UserService;
import com.ljp.xjt.service.UserRoleService;
import com.ljp.xjt.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 学生管理控制器 (管理员)
 * <p>
 * 提供学生信息的RESTful API接口，仅限管理员进行增删改查操作。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@RestController
@RequestMapping("/admin/students")
@Tag(name = "管理端 - 学生管理", description = "提供学生信息的查询、管理接口")
@RequiredArgsConstructor
@Slf4j
public class StudentAdminController {

    private final StudentService studentService;
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final GradeService gradeService;

    /**
     * [管理员] 创建新学生信息
     *
     * @param student 学生信息实体
     * @return ApiResponse<Student> 创建结果及创建后的学生信息
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建新学生", description = "创建一个新的学生记录，关联到用户表。需要管理员权限。")
    public ApiResponse<Student> createStudent(@Valid @RequestBody Student student) {
        // 1. 校验学号是否已存在
        if (studentService.checkStudentNumberExists(student.getStudentNumber(), null)) {
            return ApiResponse.error(400, "学号已存在");
        }
        // 2. 校验关联的userId是否存在
        if (student.getUserId() == null || userService.getById(student.getUserId()) == null) {
            return ApiResponse.error(400, "关联的用户ID无效或用户不存在");
        }

        Long userId = student.getUserId();

        // 检查用户是否已经有STUDENT角色
        if (!userRoleService.hasRole(userId, "STUDENT")) {
            log.info("User {} does not have student role, assigning it", userId);
            if (!userRoleService.assignRoleByCode(userId, "STUDENT")) {
                return ApiResponse.error(500, "分配学生角色失败");
            }
        }

        // 检查用户是否已关联其他学生记录
        Student existingStudent = studentService.findByUserId(userId);
        if (existingStudent != null) {
            log.warn("User {} is already associated with student record {}", userId, existingStudent.getId());
            return ApiResponse.error(400, "该用户已关联到其他学生记录");
        }

        // 3. 保存学生信息
        if (studentService.save(student)) {
            log.info("Student record created successfully for user {}", userId);
            return ApiResponse.created(student);
        }
        return ApiResponse.error(500, "学生信息创建失败");
    }

    /**
     * [管理员] 根据ID获取学生信息
     *
     * @param id 学生记录ID
     * @return ApiResponse<Student> 学生信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取学生详情", description = "根据学生记录ID查询详细信息。需要管理员权限。")
    public ApiResponse<Student> getStudentByIdAsAdmin(@Parameter(description = "学生记录ID") @PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ApiResponse.success(student);
    }

    /**
     * [管理员] 更新学生信息
     *
     * @param id      学生记录ID
     * @param student 更新后的学生信息实体
     * @return ApiResponse<Student> 更新结果及更新后的学生信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新学生信息", description = "根据学生记录ID更新信息。需要管理员权限。")
    public ApiResponse<Student> updateStudent(@Parameter(description = "学生记录ID") @PathVariable Long id,
                                            @Valid @RequestBody Student student) {
        // 1. 检查学生记录是否存在 (getStudentById会抛异常)
        Student existingStudent = studentService.getStudentById(id);
        
        // 2. 校验更新后的学号是否与其它学生冲突
        if (studentService.checkStudentNumberExists(student.getStudentNumber(), id)) {
            return ApiResponse.error(400, "学号已存在");
        }
        
        // 3. 设置ID并更新
        student.setId(id);
        student.setUserId(existingStudent.getUserId()); // userId不允许更改

        if (studentService.updateById(student)) {
            return ApiResponse.success("学生信息更新成功", studentService.getById(id));
        }
        return ApiResponse.error(500, "学生信息更新失败");
    }

    /**
     * [管理员] 根据ID删除学生信息
     *
     * @param id 学生记录ID
     * @return ApiResponse<Void> 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除学生信息", description = "根据学生记录ID删除学生信息。需要管理员权限。")
    public ApiResponse<Void> deleteStudent(@Parameter(description = "学生记录ID") @PathVariable Long id) {
        Student existingStudent = studentService.getStudentById(id);
        
        // 检查是否有关联的成绩记录
        long gradeCount = gradeService.count(new LambdaQueryWrapper<Grade>().eq(Grade::getStudentId, id));
        if (gradeCount > 0) {
            log.warn("Cannot delete student with ID {} because there are {} grade records associated", id, gradeCount);
            return ApiResponse.error(400, "该学生有关联的成绩记录，不能直接删除。");
        }
        
        // 删除学生记录
        if (!studentService.removeById(id)) {
            return ApiResponse.error(500, "学生信息删除失败");
        }
        
        log.info("Student record deleted successfully, removing student role from user {}", existingStudent.getUserId());
        userRoleService.removeRoleByCode(existingStudent.getUserId(), "STUDENT");
        
        return ApiResponse.success("学生信息删除成功", null);
    }

    /**
     * [管理员] 分页查询学生列表
     *
     * @param pageNum       当前页码
     * @param pageSize      每页数量
     * @param studentNumber 学号 (可选查询条件)
     * @param studentName   学生姓名 (可选查询条件)
     * @param classId       班级ID (可选查询条件)
     * @return ApiResponse<Page<Student>> 分页后的学生列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "分页查询学生列表", description = "可根据学号、姓名、班级ID筛选。需要管理员权限。")
    public ApiResponse<Page<Student>> listStudents(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "学号查询") @RequestParam(required = false) String studentNumber,
            @Parameter(description = "学生姓名查询") @RequestParam(required = false) String studentName,
            @Parameter(description = "班级ID查询") @RequestParam(required = false) Long classId) {

        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(studentNumber), Student::getStudentNumber, studentNumber)
                    .like(StringUtils.hasText(studentName), Student::getStudentName, studentName)
                    .eq(classId != null, Student::getClassId, classId)
                    .orderByDesc(Student::getCreatedTime);

        Page<Student> page = new Page<>(pageNum, pageSize);
        studentService.page(page, queryWrapper);
        return ApiResponse.success(page);
    }
} 