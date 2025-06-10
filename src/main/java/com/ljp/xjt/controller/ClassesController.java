package com.ljp.xjt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.dto.AssignStudentsDTO;
import com.ljp.xjt.dto.ClassDto;
import com.ljp.xjt.entity.Classes;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.entity.CourseSchedule;
import com.ljp.xjt.service.ClassesService;
import com.ljp.xjt.service.MajorService;
import com.ljp.xjt.service.StudentService;
import com.ljp.xjt.service.CourseScheduleService;
import com.ljp.xjt.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 班级管理控制器
 * <p>
 * 提供班级信息的RESTful API接口，包括增、删、改、查等操作。
 * 所有接口均需要管理员权限。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@RestController
@RequestMapping("/admin/classes")
@Tag(name = "班级管理", description = "提供班级信息的增删改查接口")
@PreAuthorize("hasRole('ADMIN')")
public class ClassesController {

    private static final Logger log = LoggerFactory.getLogger(ClassesController.class);

    private final ClassesService classesService;
    private final StudentService studentService;
    private final CourseScheduleService courseScheduleService;
    private final MajorService majorService;
    private final TeacherService teacherService;

    @Autowired
    public ClassesController(ClassesService classesService, 
                           StudentService studentService,
                           CourseScheduleService courseScheduleService,
                           MajorService majorService,
                           TeacherService teacherService) {
        this.classesService = classesService;
        this.studentService = studentService;
        this.courseScheduleService = courseScheduleService;
        this.majorService = majorService;
        this.teacherService = teacherService;
    }

    /**
     * 创建新班级
     *
     * @param classes 班级信息实体
     * @return ApiResponse<Classes> 创建结果及创建后的班级信息
     */
    @PostMapping
    @Operation(summary = "创建新班级", description = "创建一个新的班级信息")
    public ApiResponse<Classes> createClass(@Valid @RequestBody Classes classes) {
        // 1. 校验班级名称和编码是否已存在
        if (classesService.checkClassNameExists(classes.getClassName(), null)) {
            return ApiResponse.error(400, "班级名称已存在");
        }
        if (classesService.checkClassCodeExists(classes.getClassCode(), null)) {
            return ApiResponse.error(400, "班级编码已存在");
        }

        // 2. 校验外键是否存在
        if (classes.getMajorId() == null || majorService.getById(classes.getMajorId()) == null) {
            return ApiResponse.error(400, "指定的专业ID无效或不存在");
        }
        if (classes.getAdvisorTeacherId() == null || teacherService.getById(classes.getAdvisorTeacherId()) == null) {
            return ApiResponse.error(400, "指定的班主任教师ID无效或不存在");
        }

        // 3. 确保ID由后端生成，防止客户端传入ID
        classes.setId(null);
        // 4. 保存班级信息
        boolean success = classesService.save(classes);
        if (success) {
            return ApiResponse.created(classes);
        }
        return ApiResponse.error(500, "班级创建失败");
    }

    /**
     * 为班级批量添加学生
     *
     * @param classId           班级ID
     * @param assignStudentsDTO 包含学生ID列表的DTO
     * @return ApiResponse 包含操作结果
     */
    @PostMapping("/{classId}/students")
    @Operation(summary = "为班级批量添加学生", description = "将一批未分配班级的学生加入到指定班级。")
    public ApiResponse<Void> addStudentsToClass(
            @Parameter(description = "班级ID") @PathVariable Long classId,
            @Valid @RequestBody AssignStudentsDTO assignStudentsDTO) {

        // 1. 检查班级是否存在
        Classes targetClass = classesService.getById(classId);
        if (targetClass == null) {
            return ApiResponse.error(404, "指定的班级不存在");
        }

        // 2. 调用服务层处理业务逻辑
        int assignedCount = studentService.assignStudentsToClass(classId, assignStudentsDTO.getStudentIds());
        log.info("Successfully assigned {} students to class {}", assignedCount, classId);

        return ApiResponse.success("成功为班级添加 " + assignedCount + " 名学生");
    }

    /**
     * 根据ID获取班级信息
     *
     * @param id 班级ID
     * @return ApiResponse<Classes> 班级信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取班级详情", description = "根据ID查询班级详细信息")
    public ApiResponse<Classes> getClassById(@Parameter(description = "班级ID") @PathVariable Long id) {
        Classes classes = classesService.getById(id);
        if (classes != null) {
            return ApiResponse.success(classes);
        }
        return ApiResponse.notFound();
    }

    /**
     * 更新班级信息
     *
     * @param id      班级ID
     * @param classes 更新后的班级信息实体
     * @return ApiResponse<Classes> 更新结果及更新后的班级信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新班级信息", description = "根据ID更新指定班级的信息")
    public ApiResponse<Classes> updateClass(@Parameter(description = "班级ID") @PathVariable Long id,
                                          @Valid @RequestBody Classes classes) {
        // 1. 检查班级是否存在
        Classes existingClass = classesService.getById(id);
        if (existingClass == null) {
            return ApiResponse.notFound();
        }
        // 2. 校验更新后的班级名称和编码是否与其它班级冲突
        if (classesService.checkClassNameExists(classes.getClassName(), id)) {
            return ApiResponse.error(400, "班级名称已存在");
        }
        if (classesService.checkClassCodeExists(classes.getClassCode(), id)) {
            return ApiResponse.error(400, "班级编码已存在");
        }
        // 3. 设置ID并更新
        classes.setId(id);
        boolean success = classesService.updateById(classes);
        if (success) {
            return ApiResponse.success("班级更新成功", classesService.getById(id));
        }
        return ApiResponse.error(500, "班级更新失败");
    }

    /**
     * 根据ID删除班级
     *
     * @param id 班级ID
     * @return ApiResponse<Void> 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除班级", description = "根据ID删除指定班级")
    public ApiResponse<Void> deleteClass(@Parameter(description = "班级ID") @PathVariable Long id) {
        Classes existingClass = classesService.getById(id);
        if (existingClass == null) {
            return ApiResponse.notFound();
        }
        
        // 删除班级前应检查是否有学生关联，或其它关联数据
        // 1. 检查是否有学生关联到此班级
        LambdaQueryWrapper<Student> studentQuery = new LambdaQueryWrapper<>();
        studentQuery.eq(Student::getClassId, id);
        long studentCount = studentService.count(studentQuery);
        
        if (studentCount > 0) {
            log.warn("Cannot delete class with ID {} because {} students are associated with it", id, studentCount);
            return ApiResponse.error(400, "该班级下还有" + studentCount + "名学生，不能直接删除。请先转移或删除相关学生");
        }
        
        // 2. 检查是否有课程安排关联到此班级
        LambdaQueryWrapper<CourseSchedule> scheduleQuery = new LambdaQueryWrapper<>();
        scheduleQuery.eq(CourseSchedule::getClassId, id);
        long scheduleCount = courseScheduleService.count(scheduleQuery);
        
        if (scheduleCount > 0) {
            log.warn("Cannot delete class with ID {} because {} course schedules are associated with it", id, scheduleCount);
            return ApiResponse.error(400, "该班级有关联的课程安排记录，不能直接删除。请先删除相关课程安排");
        }
        
        // 执行删除操作
        boolean success = classesService.removeById(id);
        if (success) {
            log.info("Class with ID {} deleted successfully", id);
            return ApiResponse.success("班级删除成功", null);
        }
        return ApiResponse.error(500, "班级删除失败");
    }

    /**
     * 分页查询班级列表
     *
     * @param current    当前页码
     * @param size   每页数量
     * @param className  班级名称 (可选查询条件)
     * @param classCode  班级编码 (可选查询条件)
     * @param gradeYear  年级 (可选查询条件)
     * @return ApiResponse<Page<ClassDto>> 分页后的班级列表
     */
    @GetMapping
    @Operation(summary = "分页查询班级列表", description = "可根据班级名称、编码、年级进行筛选")
    public ApiResponse<Page<ClassDto>> listClasses(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "班级名称查询") @RequestParam(required = false) String className,
            @Parameter(description = "班级编码查询") @RequestParam(required = false) String classCode,
            @Parameter(description = "年级查询") @RequestParam(required = false) Integer gradeYear) {

        LambdaQueryWrapper<Classes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(className), Classes::getClassName, className)
                    .like(StringUtils.hasText(classCode), Classes::getClassCode, classCode)
                    .eq(gradeYear != null, Classes::getGradeYear, gradeYear)
                    .orderByDesc(Classes::getCreatedTime); // 默认按创建时间降序

        Page<Classes> page = new Page<>(current, size);
        Page<ClassDto> dtoPage = classesService.selectPageWithMajor(page, queryWrapper);
        return ApiResponse.success(dtoPage);
    }

    /**
     * 获取所有班级列表 (不分页)
     *
     * @return ApiResponse<List<ClassDto>> 所有班级列表
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有班级列表", description = "查询所有班级信息，不进行分页")
    public ApiResponse<List<ClassDto>> listAllClasses() {
        // 1. 获取所有班级
        List<Classes> classesList = classesService.list(new LambdaQueryWrapper<Classes>().orderByAsc(Classes::getClassName));
        if (classesList.isEmpty()) {
            return ApiResponse.success(List.of());
        }

        // 2. 提取 majorId 并查询专业信息
        List<Long> majorIds = classesList.stream().map(Classes::getMajorId).distinct().collect(Collectors.toList());
        Map<Long, String> majorIdToNameMap = classesService.getMajorIdToNameMap(majorIds);

        // 3. 转换为 DTO 列表
        List<ClassDto> dtoList = classesList.stream().map(classes -> {
            ClassDto dto = new ClassDto();
            BeanUtils.copyProperties(classes, dto);
            dto.setMajorName(majorIdToNameMap.get(classes.getMajorId()));
            return dto;
        }).collect(Collectors.toList());
        
        return ApiResponse.success(dtoList);
    }
} 