package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.dto.StudentDto;
import com.ljp.xjt.dto.TeacherClassDto;
import com.ljp.xjt.dto.TeacherCourseDto;
import com.ljp.xjt.entity.Teacher;
import com.ljp.xjt.entity.TeachingAssignment;
import com.ljp.xjt.mapper.TeacherMapper;
import com.ljp.xjt.service.GradeService;
import com.ljp.xjt.service.StudentService;
import com.ljp.xjt.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
@RequiredArgsConstructor
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    private final GradeService gradeService;
    private final StudentService studentService;

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
     * @param teacher 教师信息
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createTeacher(Teacher teacher) {
        log.info("Creating teacher: {}", teacher.getTeacherName());
        
        // 1. 检查教工号是否已存在
        if (checkTeacherNumberExists(teacher.getTeacherNumber())) {
            log.error("Teacher number already exists: {}", teacher.getTeacherNumber());
            throw new IllegalArgumentException("教工号已存在");
        }
        
        // 2. 保存教师信息
        return this.save(teacher);
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
} 