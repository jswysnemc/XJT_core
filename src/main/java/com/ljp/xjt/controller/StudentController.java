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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 学生管理控制器
 * <p>
 * 提供学生信息的RESTful API接口。管理员可进行增删改查操作，学生可查询自身信息。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@RestController
@RequestMapping("/api/students")
@Tag(name = "学生管理", description = "提供学生信息的查询、管理接口")
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);
    
    private final StudentService studentService;
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final GradeService gradeService;

    @Autowired
    public StudentController(StudentService studentService, UserService userService, 
                           UserRoleService userRoleService, GradeService gradeService) {
        this.studentService = studentService;
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.gradeService = gradeService;
    }

    /**
     * [管理员] 创建新学生信息
     *
     * @param student 学生信息实体
     * @return ApiResponse<Student> 创建结果及创建后的学生信息
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建新学生(管理员)", description = "创建一个新的学生记录，关联到用户表。需要管理员权限。")
    public ApiResponse<Student> createStudent(@Valid @RequestBody Student student) {
        // 1. 校验学号是否已存在
        if (studentService.checkStudentNumberExists(student.getStudentNumber(), null)) {
            return ApiResponse.error(400, "学号已存在");
        }
        // 2. 校验关联的userId是否存在且为学生角色 (这里简化，实际可能需要更复杂校验)
        if (student.getUserId() == null || userService.getById(student.getUserId()) == null) {
             return ApiResponse.error(400, "关联的用户ID无效或用户不存在");
        }
        
        // 校验用户角色是否为学生，以及用户是否已被其他学生记录关联
        Long userId = student.getUserId();
        
        // 检查用户是否已经有STUDENT角色
        boolean hasStudentRole = userRoleService.hasRole(userId, "STUDENT");
        if (!hasStudentRole) {
            log.info("User {} does not have student role, assigning it", userId);
            boolean assigned = userRoleService.assignRoleByCode(userId, "STUDENT");
            if (!assigned) {
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
        boolean success = studentService.save(student);
        if (success) {
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
    @Operation(summary = "获取学生详情(管理员)", description = "根据学生记录ID查询详细信息。需要管理员权限。")
    public ApiResponse<Student> getStudentByIdAsAdmin(@Parameter(description = "学生记录ID") @PathVariable Long id) {
        Student student = studentService.getById(id);
        if (student != null) {
            return ApiResponse.success(student);
        }
        return ApiResponse.notFound();
    }
    
    /**
     * [学生] 获取当前登录学生的个人信息
     *
     * @return ApiResponse<Student> 当前登录学生的信息
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "获取当前学生信息(学生)", description = "查询当前登录学生的详细信息。需要学生权限。")
    public ApiResponse<Student> getCurrentStudentInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        User currentUser = userService.findByUsername(currentUserName);

        if (currentUser == null) {
            return ApiResponse.error(404, "无法获取当前用户信息");
        }

        Student student = studentService.findByUserId(currentUser.getId());
        if (student != null) {
            return ApiResponse.success(student);
        }
        return ApiResponse.error(404, "未找到当前用户的学生信息");
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
    @Operation(summary = "更新学生信息(管理员)", description = "根据学生记录ID更新信息。需要管理员权限。")
    public ApiResponse<Student> updateStudent(@Parameter(description = "学生记录ID") @PathVariable Long id,
                                            @Valid @RequestBody Student student) {
        // 1. 检查学生记录是否存在
        Student existingStudent = studentService.getById(id);
        if (existingStudent == null) {
            return ApiResponse.notFound();
        }
        // 2. 校验更新后的学号是否与其它学生冲突
        if (studentService.checkStudentNumberExists(student.getStudentNumber(), id)) {
            return ApiResponse.error(400, "学号已存在");
        }
        // 3. 设置ID并更新
        student.setId(id);
        // userId 通常在创建学生记录时确定，一般不允许随意更改，此处保持不变或根据业务需求处理
        student.setUserId(existingStudent.getUserId()); 

        boolean success = studentService.updateById(student);
        if (success) {
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
    @Operation(summary = "删除学生信息(管理员)", description = "根据学生记录ID删除学生信息。需要管理员权限。")
    public ApiResponse<Void> deleteStudent(@Parameter(description = "学生记录ID") @PathVariable Long id) {
        Student existingStudent = studentService.getById(id);
        if (existingStudent == null) {
            return ApiResponse.notFound();
        }
        
        // 删除学生前应检查是否有成绩等关联数据
        Long userId = existingStudent.getUserId();
        Long studentId = existingStudent.getId();
        
        // 检查是否有关联的成绩记录
        LambdaQueryWrapper<Grade> gradeQuery = new LambdaQueryWrapper<>();
        gradeQuery.eq(Grade::getStudentId, studentId);
        long gradeCount = gradeService.count(gradeQuery);
        
        if (gradeCount > 0) {
            log.warn("Cannot delete student with ID {} because there are {} grade records associated", studentId, gradeCount);
            return ApiResponse.error(400, "该学生有关联的成绩记录，不能直接删除。请先删除相关成绩记录或联系系统管理员");
        }
        
        // 删除学生记录
        boolean success = studentService.removeById(id);
        if (!success) {
            return ApiResponse.error(500, "学生信息删除失败");
        }
        
        // 不直接删除用户账号，只移除学生角色
        // 实际业务中可能需要根据用户表中的其他字段进一步判断是否应该删除用户账号
        log.info("Student record deleted successfully, removing student role from user {}", userId);
        userRoleService.removeRoleByCode(userId, "STUDENT");
        
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
    @Operation(summary = "分页查询学生列表(管理员)", description = "可根据学号、姓名、班级ID筛选。需要管理员权限。")
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