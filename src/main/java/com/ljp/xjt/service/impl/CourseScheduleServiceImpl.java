package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.entity.CourseSchedule;
import com.ljp.xjt.mapper.CourseScheduleMapper;
import com.ljp.xjt.service.CourseScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 课程安排服务实现类
 * <p>
 * 实现课程安排相关的业务逻辑
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Slf4j
@Service
public class CourseScheduleServiceImpl extends ServiceImpl<CourseScheduleMapper, CourseSchedule> 
        implements CourseScheduleService {
    
    /**
     * 检查班级是否有课程安排
     *
     * @param classId 班级ID
     * @return 是否有课程安排
     */
    @Override
    public boolean hasSchedulesByClassId(Long classId) {
        if (classId == null) {
            return false;
        }
        
        LambdaQueryWrapper<CourseSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseSchedule::getClassId, classId);
        
        return count(queryWrapper) > 0;
    }
    
    /**
     * 检查教师是否有课程安排
     *
     * @param teacherId 教师ID
     * @return 是否有课程安排
     */
    @Override
    public boolean hasSchedulesByTeacherId(Long teacherId) {
        if (teacherId == null) {
            return false;
        }
        
        LambdaQueryWrapper<CourseSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseSchedule::getTeacherId, teacherId);
        
        return count(queryWrapper) > 0;
    }
    
    /**
     * 检查课程是否有安排记录
     *
     * @param courseId 课程ID
     * @return 是否有课程安排
     */
    @Override
    public boolean hasSchedulesByCourseId(Long courseId) {
        if (courseId == null) {
            return false;
        }
        
        LambdaQueryWrapper<CourseSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseSchedule::getCourseId, courseId);
        
        return count(queryWrapper) > 0;
    }
} 