package com.ljp.xjt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljp.xjt.entity.TeacherCourse;

import java.util.List;

/**
 * 教师课程服务接口
 * <p>
 * 提供教师课程关联的业务操作
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
public interface TeacherCourseService extends IService<TeacherCourse> {

    /**
     * 根据教师ID和课程ID查询教师课程关联
     *
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @return 教师课程关联列表
     */
    List<TeacherCourse> getByTeacherAndCourse(Long teacherId, Long courseId);
    
    /**
     * 根据教师ID查询教师课程关联
     *
     * @param teacherId 教师ID
     * @return 教师课程关联列表
     */
    List<TeacherCourse> getByTeacherId(Long teacherId);
    
    /**
     * 根据课程ID查询教师课程关联
     *
     * @param courseId 课程ID
     * @return 教师课程关联列表
     */
    List<TeacherCourse> getByCourseId(Long courseId);
    
    /**
     * 根据班级ID查询教师课程关联
     *
     * @param classId 班级ID
     * @return 教师课程关联列表
     */
    List<TeacherCourse> getByClassId(Long classId);
} 