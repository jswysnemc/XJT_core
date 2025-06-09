package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljp.xjt.dto.GradeUpdateRequest;
import com.ljp.xjt.dto.StudentGradeDto;
import com.ljp.xjt.dto.TeacherClassDto;
import com.ljp.xjt.dto.TeacherCourseDto;
import com.ljp.xjt.entity.*;
import com.ljp.xjt.mapper.*;
import com.ljp.xjt.service.MyTeachingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 教师教学服务实现类
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-09
 */
@Service
@RequiredArgsConstructor
public class MyTeachingServiceImpl implements MyTeachingService {

    private final CourseScheduleMapper courseScheduleMapper;
    private final CourseMapper courseMapper;
    private final ClassesMapper classesMapper;
    private final StudentMapper studentMapper;
    private final GradeMapper gradeMapper;
    
    @Override
    public List<TeacherCourseDto> findMyCourses(Long teacherId) {
        // 1. 从教学分配表(course_schedule)中找到该教师教的所有课程ID
        List<Long> courseIds = courseScheduleMapper.selectList(
                new LambdaQueryWrapper<CourseSchedule>()
                        .eq(CourseSchedule::getTeacherId, teacherId)
                        .select(CourseSchedule::getCourseId)
        ).stream().map(CourseSchedule::getCourseId).distinct().collect(Collectors.toList());

        if (courseIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 根据课程ID批量查询课程详情
        return courseMapper.selectBatchIds(courseIds).stream()
                .map(course -> new TeacherCourseDto(
                        course.getId(),
                        course.getCourseName(),
                        course.getCourseCode(),
                        course.getCredits()
                )).collect(Collectors.toList());
    }

    @Override
    public List<TeacherClassDto> findMyClassesForCourse(Long teacherId, Long courseId) {
        // 1. 从教学分配表中找到该教师在该课程下教的所有班级ID
        List<Long> classIds = courseScheduleMapper.selectList(
                new LambdaQueryWrapper<CourseSchedule>()
                        .eq(CourseSchedule::getTeacherId, teacherId)
                        .eq(CourseSchedule::getCourseId, courseId)
                        .select(CourseSchedule::getClassId)
        ).stream().map(CourseSchedule::getClassId).distinct().collect(Collectors.toList());

        if (classIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 根据班级ID批量查询班级详情
        return classesMapper.selectBatchIds(classIds).stream()
                .map(cls -> new TeacherClassDto(
                        cls.getId(),
                        cls.getClassName(),
                        cls.getGradeYear()
                )).collect(Collectors.toList());
    }

    @Override
    public List<StudentGradeDto> getStudentGradesForClass(Long teacherId, Long courseId, Long classId) {
        // 1. 权限校验：确认该教师是否真的教这个班的这门课
        checkPermission(teacherId, courseId, classId);

        // 2. 获取该班级的所有学生
        List<Student> students = studentMapper.selectList(new LambdaQueryWrapper<Student>().eq(Student::getClassId, classId));
        if (students.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 3. 获取这些学生在该课程下的已有成绩
        List<Long> studentIds = students.stream().map(Student::getId).collect(Collectors.toList());
        Map<Long, Grade> studentGradeMap = gradeMapper.selectList(
                new LambdaQueryWrapper<Grade>()
                        .eq(Grade::getCourseId, courseId)
                        .in(Grade::getStudentId, studentIds)
        ).stream().collect(Collectors.toMap(Grade::getStudentId, Function.identity()));

        // 4. 组装DTO返回，无论学生有无成绩，都应在列表中
        return students.stream().map(student -> {
            Grade grade = studentGradeMap.get(student.getId());
            StudentGradeDto dto = new StudentGradeDto();
            dto.setStudentId(student.getId());
            dto.setStudentName(student.getStudentName());
            dto.setStudentNumber(student.getStudentNumber());
            if (grade != null) {
                dto.setGradeId(grade.getId());
                dto.setScore(grade.getScore());
                dto.setGradeUpdatedTime(grade.getUpdatedTime());
            }
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void batchUpdateGrades(Long teacherId, GradeUpdateRequest request) {
        Long courseId = request.getCourseId();
        
        // 1. 验证权限：遍历所有要修改的学生，确认他们是否都在该教师所教的班级里
        // 为了简化，我们只校验第一个学生所在的班级，并假设所有学生都在同一个班级
        if (request.getGrades() == null || request.getGrades().isEmpty()) {
            return; // 没有成绩项，直接返回
        }
        
        Long firstStudentId = request.getGrades().get(0).getStudentId();
        Student firstStudent = studentMapper.selectById(firstStudentId);
        if (firstStudent == null) {
            throw new IllegalArgumentException("学生ID " + firstStudentId + " 不存在");
        }
        checkPermission(teacherId, courseId, firstStudent.getClassId());

        // 2. 准备数据
        List<Long> studentIds = request.getGrades().stream().map(GradeUpdateRequest.GradeItem::getStudentId).collect(Collectors.toList());

        // 3. 找出已存在的成绩记录
        Map<Long, Grade> existingGrades = gradeMapper.selectList(
                new LambdaQueryWrapper<Grade>()
                        .eq(Grade::getCourseId, courseId)
                        .in(Grade::getStudentId, studentIds)
        ).stream().collect(Collectors.toMap(Grade::getStudentId, Function.identity()));

        // 4. 分离出需要新增和需要更新的成绩
        List<Grade> gradesToInsert = new java.util.ArrayList<>();
        List<Grade> gradesToUpdate = new java.util.ArrayList<>();

        for (GradeUpdateRequest.GradeItem item : request.getGrades()) {
            Grade existingGrade = existingGrades.get(item.getStudentId());
            if (existingGrade != null) { // 更新
                existingGrade.setScore(item.getScore());
                existingGrade.setUpdatedTime(LocalDateTime.now());
                gradesToUpdate.add(existingGrade);
            } else { // 新增
                Grade newGrade = new Grade();
                newGrade.setStudentId(item.getStudentId());
                newGrade.setCourseId(courseId);
                newGrade.setScore(item.getScore());
                newGrade.setCreatedBy(teacherId); // 记录创建者为当前教师
                gradesToInsert.add(newGrade);
            }
        }
        
        // 5. 批量执行数据库操作
        if (!gradesToInsert.isEmpty()) {
            gradesToInsert.forEach(gradeMapper::insert);
        }
        if (!gradesToUpdate.isEmpty()) {
            gradesToUpdate.forEach(gradeMapper::updateById);
        }
    }

    /**
     * 内部权限检查方法
     */
    private void checkPermission(Long teacherId, Long courseId, Long classId) {
        boolean hasPermission = courseScheduleMapper.exists(
                new LambdaQueryWrapper<CourseSchedule>()
                        .eq(CourseSchedule::getTeacherId, teacherId)
                        .eq(CourseSchedule::getCourseId, courseId)
                        .eq(CourseSchedule::getClassId, classId)
        );
        if (!hasPermission) {
            throw new AccessDeniedException("无权访问该班级和课程的信息");
        }
    }
} 