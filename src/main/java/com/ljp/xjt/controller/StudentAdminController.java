package com.ljp.xjt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.dto.StudentCreateDTO;
import com.ljp.xjt.dto.StudentDTO;
import com.ljp.xjt.dto.StudentUpdateDTO;
import com.ljp.xjt.dto.UnboundUserDTO;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.entity.Grade;
import com.ljp.xjt.service.ClassesService;
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

import java.util.List;

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
    private final ClassesService classesService;

    /**
     * [管理员] 获取未绑定任何学生记录的用户列表
     * <p>
     * 此接口用于在创建学生时，提供一个可选的、已存在但未被绑定的用户列表。
     * 仅返回拥有 "STUDENT" 角色的用户。
     *
     * @return ApiResponse<List<UnboundUserDTO>>
     */
    @GetMapping("/unbound-users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取未绑定的学生用户", description = "查询所有拥有学生角色但未关联任何学生信息的用户列表。")
    public ApiResponse<List<UnboundUserDTO>> getUnboundStudentUsers() {
        return ApiResponse.success(userService.findUnboundStudentUsers());
    }

    /**
     * [管理员] 创建新学生档案
     * <p>
     *     此接口仅创建学生的基本档案信息，不与任何用户或班级绑定。
     *     创建后，可以后续进行用户绑定和班级分配。
     * </p>
     *
     * @param studentDTO 学生创建数据传输对象
     * @return ApiResponse<Student> 创建结果及创建后的学生信息
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建新学生档案", description = "创建一个新的学生记录，此时不关联用户和班级。需要管理员权限。")
    public ApiResponse<Student> createStudent(@Valid @RequestBody StudentCreateDTO studentDTO) {
        // 1. 校验学号是否已存在
        if (studentService.checkStudentNumberExists(studentDTO.getStudentNumber(), null)) {
            return ApiResponse.error(400, "学号已存在");
        }

        // 2. 将 DTO 转换为 Student 实体
        Student student = new Student();
        student.setStudentNumber(studentDTO.getStudentNumber());
        student.setStudentName(studentDTO.getStudentName());
        student.setGender(studentDTO.getGender());
        student.setBirthDate(studentDTO.getBirthDate());
        // 根据业务要求，初始创建时，不关联用户和班级
        student.setUserId(null);
        student.setClassId(null);

        // 3. 保存学生信息
        if (studentService.save(student)) {
            log.info("Student record created successfully with student number {}", student.getStudentNumber());
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
     * @param studentDTO 更新后的学生信息实体
     * @return ApiResponse<Student> 更新结果及更新后的学生信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新学生信息", description = "根据学生记录ID更新信息。可用于分配班级。需要管理员权限。")
    public ApiResponse<Student> updateStudent(@Parameter(description = "学生记录ID") @PathVariable Long id,
                                            @Valid @RequestBody StudentUpdateDTO studentDTO) {
        // 1. 检查学生记录是否存在
        Student existingStudent = studentService.getById(id);
        if (existingStudent == null) {
            return ApiResponse.error(404, "找不到指定的学生记录");
        }
        
        // 2. 校验更新后的学号是否与其它学生冲突
        if (studentService.checkStudentNumberExists(studentDTO.getStudentNumber(), id)) {
            return ApiResponse.error(400, "学号已存在");
        }

        // 3. 如果提供了班级ID，校验其有效性
        if (studentDTO.getClassId() != null && classesService.getById(studentDTO.getClassId()) == null) {
            return ApiResponse.error(400, "指定的班级ID无效或不存在");
        }
        
        // 4. 使用 DTO 更新实体
        existingStudent.setStudentNumber(studentDTO.getStudentNumber());
        existingStudent.setStudentName(studentDTO.getStudentName());
        existingStudent.setGender(studentDTO.getGender());
        existingStudent.setBirthDate(studentDTO.getBirthDate());
        existingStudent.setClassId(studentDTO.getClassId());
        // userId 不允许通过此接口修改

        if (studentService.updateById(existingStudent)) {
            return ApiResponse.success("学生信息更新成功", existingStudent);
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
        Student existingStudent = studentService.getById(id);
        if (existingStudent == null) {
            return ApiResponse.error(404, "找不到指定的学生记录");
        }
        
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
        
        // 如果学生已关联用户，则移除用户的学生角色
        if (existingStudent.getUserId() != null) {
            log.info("Student record {} deleted successfully, removing student role from user {}", id, existingStudent.getUserId());
            userRoleService.removeRoleByCode(existingStudent.getUserId(), "STUDENT");
        } else {
            log.info("Student record {} deleted successfully. No user was associated.", id);
        }
        
        return ApiResponse.success("学生信息删除成功", null);
    }

    /**
     * [管理员] 分页查询学生列表
     *
     * @param current       当前页码
     * @param size      每页数量
     * @param studentNumber 学号 (可选查询条件)
     * @param studentName   学生姓名 (可选查询条件)
     * @param classId       班级ID (可选查询条件)
     * @return ApiResponse<IPage<StudentDTO>> 分页后的学生列表（包含班级名称）
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "分页查询学生列表", description = "可根据学号、姓名、班级ID筛选。需要管理员权限。")
    public ApiResponse<IPage<StudentDTO>> listStudents(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "学号查询") @RequestParam(required = false) String studentNumber,
            @Parameter(description = "学生姓名查询") @RequestParam(required = false) String studentName,
            @Parameter(description = "班级ID查询") @RequestParam(required = false) Long classId) {

        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(studentNumber), Student::getStudentNumber, studentNumber)
                    .like(StringUtils.hasText(studentName), Student::getStudentName, studentName)
                    .eq(classId != null, Student::getClassId, classId)
                    .orderByDesc(Student::getCreatedTime);

        Page<Student> page = new Page<>(current, size);
        IPage<StudentDTO> studentDTOPage = studentService.selectStudentPage(page, queryWrapper);
        return ApiResponse.success(studentDTOPage);
    }
} 