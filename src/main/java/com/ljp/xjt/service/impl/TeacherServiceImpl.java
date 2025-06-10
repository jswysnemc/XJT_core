package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.dto.StudentDto;
import com.ljp.xjt.dto.TeacherClassDto;
import com.ljp.xjt.dto.TeacherCourseDto;
import com.ljp.xjt.dto.TeacherProfileDto;
import com.ljp.xjt.dto.TeacherProfileUpdateRequestDto;
import com.ljp.xjt.dto.TeachingStatisticsDto;
import com.ljp.xjt.dto.BatchGradeEntryDto;
import com.ljp.xjt.dto.BatchGradeResponseDto;
import com.ljp.xjt.dto.FailureDetailDto;
import com.ljp.xjt.dto.TeacherCreateDTO;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.entity.Teacher;
import com.ljp.xjt.entity.TeachingAssignment;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.mapper.StudentMapper;
import com.ljp.xjt.mapper.TeacherMapper;
import com.ljp.xjt.service.GradeService;
import com.ljp.xjt.service.StudentService;
import com.ljp.xjt.service.TeacherService;
import com.ljp.xjt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 教师服务实现类
 * <p>
 * 实现教师管理相关业务逻辑处理
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Slf4j
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    private final GradeService gradeService;
    private final UserService userService;
    private final StudentService studentService;
    private final StudentMapper studentMapper;

    public TeacherServiceImpl(GradeService gradeService, UserService userService, StudentService studentService, StudentMapper studentMapper) {
        this.gradeService = gradeService;
        this.userService = userService;
        this.studentService = studentService;
        this.studentMapper = studentMapper;
    }

    /**
     * 分页查询教师列表
     *
     * @param page 分页参数
     * @param teacherName 教师姓名(模糊查询)
     * @param departmentId 部门ID
     * @return 教师分页列表
     */
    @Override
    public IPage<Teacher> getTeacherList(Page<Teacher> page, String teacherName, Long departmentId) {
        log.info("Query teacher list with params: teacherName={}, departmentId={}", teacherName, departmentId);
        return this.baseMapper.selectTeacherList(page, teacherName, departmentId);
    }

    /**
     * 创建教师
     *
     * @param teacherCreateDTO 教师创建信息
     * @return 创建后的教师实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Teacher createTeacher(TeacherCreateDTO teacherCreateDTO) {
        log.info("Creating teacher profile: {}", teacherCreateDTO.getTeacherName());

        // 1. 检查教工号是否已存在
        if (checkTeacherNumberExists(teacherCreateDTO.getTeacherNumber())) {
            log.error("Teacher number already exists: {}", teacherCreateDTO.getTeacherNumber());
            throw new IllegalArgumentException("教工号已存在");
        }
        
        // TODO: 部门存在性校验。当前缺少Department模块，暂时由数据库外键约束保证。

        // 2. 转换DTO为实体
        Teacher teacher = new Teacher();
        teacher.setTeacherNumber(teacherCreateDTO.getTeacherNumber());
        teacher.setTeacherName(teacherCreateDTO.getTeacherName());
        teacher.setGender(teacherCreateDTO.getGender());
        teacher.setTitle(teacherCreateDTO.getTitle());
        teacher.setDepartmentId(teacherCreateDTO.getDepartmentId());
        // 将 userId 显式设置为 null，因为此时只是创建教师档案，还未关联用户
        teacher.setUserId(null);

        // 3. 保存教师信息
        this.save(teacher);
        return teacher;
    }

    /**
     * 更新教师
     *
     * @param teacher 教师信息
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTeacher(Teacher teacher) {
        log.info("Updating teacher: {}", teacher.getId());
        
        // 1. 获取原教师信息
        Teacher existingTeacher = this.getById(teacher.getId());
        if (existingTeacher == null) {
            log.error("Teacher not found: {}", teacher.getId());
            throw new IllegalArgumentException("教师不存在");
        }
        
        // 2. 如果修改了教工号，检查是否已存在
        if (!existingTeacher.getTeacherNumber().equals(teacher.getTeacherNumber()) && 
            checkTeacherNumberExists(teacher.getTeacherNumber())) {
            log.error("Teacher number already exists: {}", teacher.getTeacherNumber());
            throw new IllegalArgumentException("教工号已存在");
        }
        
        // 3. 更新教师信息
        return this.updateById(teacher);
    }

    /**
     * 查找教师信息
     *
     * @param id 教师ID
     * @return 教师信息
     */
    @Override
    public Teacher getTeacherById(Long id) {
        log.info("Getting teacher by id: {}", id);
        return this.getById(id);
    }

    /**
     * 根据用户ID查询教师信息
     *
     * @param userId 用户ID
     * @return 教师信息
     */
    @Override
    public Teacher getTeacherByUserId(Long userId) {
        log.info("Getting teacher by userId: {}", userId);
        return this.baseMapper.selectByUserId(userId);
    }

    /**
     * 检查教工号是否已存在
     *
     * @param teacherNumber 教工号
     * @return 是否存在
     */
    @Override
    public boolean checkTeacherNumberExists(String teacherNumber) {
        log.info("Checking if teacher number exists: {}", teacherNumber);
        return this.baseMapper.checkTeacherNumberExists(teacherNumber) > 0;
    }

    /**
     * 获取所有教师(用于下拉选择)
     *
     * @return 教师列表
     */
    @Override
    public List<Teacher> getAllTeachers() {
        log.info("Getting all teachers");
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Teacher::getId, Teacher::getTeacherName, Teacher::getTeacherNumber);
        wrapper.orderByAsc(Teacher::getTeacherNumber);
        return this.list(wrapper);
    }

    /**
     * 根据用户ID查询该教师所教授的课程列表
     *
     * @param userId 用户ID
     * @return 教师课程列表
     */
    @Override
    public List<TeacherCourseDto> findCoursesByUserId(Long userId) {
        log.info("Finding courses for user id: {}", userId);
        return this.baseMapper.findCoursesByUserId(userId);
    }

    /**
     * 根据用户ID和课程ID，查询该教师在该课程下所教授的班级列表
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 教师班级列表
     */
    @Override
    public List<TeacherClassDto> findClassesByCourseId(Long userId, Long courseId) {
        log.info("Finding classes for user id: {} and course id: {}", userId, courseId);
        return this.baseMapper.findClassesByCourseId(userId, courseId);
    }

    /**
     * 根据用户ID、班级ID和课程ID，查询学生列表
     *
     * @param userId   用户ID
     * @param classId  班级ID
     * @param courseId 课程ID
     * @return 学生列表
     */
    @Override
    public List<StudentDto> findStudentsByClassAndCourse(Long userId, Long classId, Long courseId) {
        log.info("Finding students for user id: {}, class id: {} and course id: {}", userId, classId, courseId);
        return this.baseMapper.findStudentsByClassAndCourse(userId, classId, courseId);
    }

    /**
     * 更新学生成绩
     *
     * @param userId    当前操作的教师用户ID
     * @param courseId  课程ID
     * @param classId   班级ID
     * @param studentId 学生ID
     * @param score     新的分数
     * @return 操作是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateGrade(Long userId, Long courseId, Long classId, Long studentId, BigDecimal score) {
        // 1. 获取教师实体ID
        Teacher teacher = baseMapper.selectByUserId(userId);
        if (teacher == null) {
            throw new IllegalArgumentException("无法找到对应的教师信息");
        }
        Long teacherId = teacher.getId();

        // 2. 权限校验：并获取学期、学年信息
        TeachingAssignment assignment = gradeService.verifyAndGetTeachingAssignment(teacherId, courseId, classId);
        String semester = assignment.getSemester();
        Integer year = assignment.getYear();

        // 3. 学生归属校验：检查学生是否属于该班级
        if (!studentService.isStudentInClass(studentId, classId)) {
            throw new IllegalArgumentException("该学生不属于指定班级");
        }

        // 4. 更新或插入成绩
        return gradeService.upsertGrade(studentId, courseId, score, teacherId, semester, year);
    }

    @Override
    public TeacherProfileDto getTeacherProfileByUserId(Long userId) {
        return baseMapper.findTeacherProfileByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTeacherProfile(Long userId, TeacherProfileUpdateRequestDto updateDto) {
        boolean teacherUpdated = false;
        boolean userUpdated = false;

        // 1. 根据userId找到教师实体
        Teacher teacher = this.getTeacherByUserId(userId);
        if (teacher == null) {
            log.warn("Attempted to update profile for a non-existent teacher with user ID: {}", userId);
            return false;
        }

        // 2. 更新教师表(teachers)中的信息
        if (StringUtils.hasText(updateDto.getTeacherName()) && !updateDto.getTeacherName().equals(teacher.getTeacherName())) {
            teacher.setTeacherName(updateDto.getTeacherName());
            teacherUpdated = true;
        }

        if (teacherUpdated) {
            this.updateById(teacher);
        }

        // 3. 更新用户表(users)中的信息
        User user = userService.getById(userId);
        if (user == null) {
            log.error("Data inconsistency: Teacher found but corresponding User not found for ID: {}", userId);
            // 抛出异常以回滚事务
            throw new IllegalStateException("用户数据不存在，请联系管理员");
        }

        if (StringUtils.hasText(updateDto.getEmail()) && !updateDto.getEmail().equals(user.getEmail())) {
            // 在实际应用中，可能需要检查邮箱是否已被其他用户使用
            user.setEmail(updateDto.getEmail());
            userUpdated = true;
        }

        if (StringUtils.hasText(updateDto.getPhone()) && !updateDto.getPhone().equals(user.getPhone())) {
            user.setPhone(updateDto.getPhone());
            userUpdated = true;
        }

        if (userUpdated) {
            userService.updateById(user);
        }

        // 只要有任何一部分更新了，就认为操作是成功的
        return teacherUpdated || userUpdated;
    }

    @Override
    public TeachingStatisticsDto getTeachingStatistics(Long teacherId) {
        // 1. 分别调用Mapper方法获取各项统计数据
        Long totalCourses = baseMapper.countTaughtCourses(teacherId);
        Long totalClasses = baseMapper.countTaughtClasses(teacherId);
        Long totalStudents = baseMapper.countTaughtStudents(teacherId);
        BigDecimal averageScore = baseMapper.calculateAverageScore(teacherId);

        // 2. 使用Builder模式构建DTO对象
        return TeachingStatisticsDto.builder()
                .totalCourses(totalCourses != null ? totalCourses : 0L)
                .totalClasses(totalClasses != null ? totalClasses : 0L)
                .totalStudents(totalStudents != null ? totalStudents : 0L)
                .averageScore(averageScore) // 如果没有分数，AVG会返回null，这符合预期
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchGradeResponseDto batchUpdateGrades(Long userId, Long courseId, Long classId, List<BatchGradeEntryDto> gradeEntries) {
        // 1. 获取教师ID及校验权限，并获取学期、学年信息
        Teacher teacher = this.getTeacherByUserId(userId);
        if (teacher == null) {
            throw new IllegalArgumentException("无法找到对应的教师信息");
        }
        Long teacherId = teacher.getId();
        TeachingAssignment assignment = gradeService.verifyAndGetTeachingAssignment(teacherId, courseId, classId);
        String semester = assignment.getSemester();
        Integer year = assignment.getYear();

        // 2. 初始化结果统计对象
        List<FailureDetailDto> failures = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        // 3. 遍历处理每一个成绩条目
        for (BatchGradeEntryDto entry : gradeEntries) {
            // 3.1. 根据学号和班级ID查找学生
            Student student = studentMapper.findStudentByNumberAndClassId(entry.getStudentNumber(), classId);

            // 3.2. 数据校验
            if (student == null) {
                failures.add(new FailureDetailDto(entry.getStudentNumber(), entry.getScore(), "该学号不存在或学生不属于该班级"));
                failureCount++;
                continue;
            }
            
            // Score的范围校验已由DTO的注解完成，这里可以省略，但保留逻辑清晰度
            // if (entry.getScore().compareTo(BigDecimal.ZERO) < 0 || entry.getScore().compareTo(new BigDecimal("100")) > 0) { ... }

            // 3.3. 更新或插入成绩
            try {
                boolean result = gradeService.upsertGrade(student.getId(), courseId, entry.getScore(), teacherId, semester, year);
                if (result) {
                    successCount++;
                } else {
                    // upsertGrade 内部实现决定了它通常不会返回false，除非有未捕获的异常
                    failures.add(new FailureDetailDto(entry.getStudentNumber(), entry.getScore(), "更新成绩失败"));
                    failureCount++;
                }
            } catch (Exception e) {
                log.error("Error updating grade for student {}: {}", entry.getStudentNumber(), e.getMessage());
                failures.add(new FailureDetailDto(entry.getStudentNumber(), entry.getScore(), "服务器内部错误"));
                failureCount++;
            }
        }

        // 4. 构建响应DTO
        return BatchGradeResponseDto.builder()
                .successCount(successCount)
                .failureCount(failureCount)
                .failures(failures)
                .build();
    }
} 