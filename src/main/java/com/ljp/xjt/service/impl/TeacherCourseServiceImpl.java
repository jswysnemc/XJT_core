package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.entity.TeacherCourse;
import com.ljp.xjt.mapper.TeacherCourseMapper;
import com.ljp.xjt.service.TeacherCourseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教师课程服务实现类
 * <p>
 * 实现教师课程关联的业务操作
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Service
public class TeacherCourseServiceImpl extends ServiceImpl<TeacherCourseMapper, TeacherCourse>
        implements TeacherCourseService {

    /**
     * 根据教师ID和课程ID查询教师课程关联
     *
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @return 教师课程关联列表
     */
    @Override
    public List<TeacherCourse> getByTeacherAndCourse(Long teacherId, Long courseId) {
        LambdaQueryWrapper<TeacherCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherCourse::getTeacherId, teacherId)
                   .eq(TeacherCourse::getCourseId, courseId);
        return list(queryWrapper);
    }
    
    /**
     * 根据教师ID查询教师课程关联
     *
     * @param teacherId 教师ID
     * @return 教师课程关联列表
     */
    @Override
    public List<TeacherCourse> getByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherCourse::getTeacherId, teacherId);
        return list(queryWrapper);
    }
    
    /**
     * 根据课程ID查询教师课程关联
     *
     * @param courseId 课程ID
     * @return 教师课程关联列表
     */
    @Override
    public List<TeacherCourse> getByCourseId(Long courseId) {
        LambdaQueryWrapper<TeacherCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherCourse::getCourseId, courseId);
        return list(queryWrapper);
    }
    
    /**
     * 根据班级ID查询教师课程关联
     *
     * @param classId 班级ID
     * @return 教师课程关联列表
     */
    @Override
    public List<TeacherCourse> getByClassId(Long classId) {
        LambdaQueryWrapper<TeacherCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherCourse::getClassId, classId);
        return list(queryWrapper);
    }
} 