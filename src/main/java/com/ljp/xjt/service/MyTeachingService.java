package com.ljp.xjt.service;

import com.ljp.xjt.dto.GradeUpdateRequest;
import com.ljp.xjt.dto.StudentGradeDto;
import com.ljp.xjt.dto.TeacherClassDto;
import com.ljp.xjt.dto.TeacherCourseDto;

import java.util.List;

/**
 * 教师教学服务接口
 * <p>
 * 定义了围绕教师个人教学活动的业务逻辑，
 * 例如查询其所授课程、班级以及管理学生成绩等。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-09
 */
public interface MyTeachingService {

    /**
     * 根据教师ID获取其所教授的课程列表
     *
     * @param teacherId 教师ID
     * @return List<TeacherCourseDto> 课程列表
     */
    List<TeacherCourseDto> findMyCourses(Long teacherId);

    /**
     * 根据教师ID和课程ID获取该教师在该课程下所教授的班级列表
     *
     * @param teacherId 教师ID
     * @param courseId  课程ID
     * @return List<TeacherClassDto> 班级列表
     */
    List<TeacherClassDto> findMyClassesForCourse(Long teacherId, Long courseId);

    /**
     * 获取指定班级和课程的学生成绩单（用于教师录入和查看）
     *
     * @param teacherId 教师ID，用于权限校验
     * @param courseId  课程ID
     * @param classId   班级ID
     * @return List<StudentGradeDto> 学生成绩列表
     */
    List<StudentGradeDto> getStudentGradesForClass(Long teacherId, Long courseId, Long classId);

    /**
     * 批量更新或录入学生成绩
     *
     * @param teacherId 教师ID，用于权限校验
     * @param request   包含课程ID和成绩列表的请求体
     */
    void batchUpdateGrades(Long teacherId, GradeUpdateRequest request);
} 