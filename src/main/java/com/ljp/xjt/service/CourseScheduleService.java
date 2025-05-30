package com.ljp.xjt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljp.xjt.entity.CourseSchedule;

/**
 * 课程安排服务接口
 * <p>
 * 提供课程安排相关业务逻辑处理，包括安排查询、增加、修改等功能
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
public interface CourseScheduleService extends IService<CourseSchedule> {
    
    /**
     * 检查班级是否有课程安排
     *
     * @param classId 班级ID
     * @return 是否有课程安排
     */
    boolean hasSchedulesByClassId(Long classId);
    
    /**
     * 检查教师是否有课程安排
     *
     * @param teacherId 教师ID
     * @return 是否有课程安排
     */
    boolean hasSchedulesByTeacherId(Long teacherId);
    
    /**
     * 检查课程是否有安排记录
     *
     * @param courseId 课程ID
     * @return 是否有课程安排
     */
    boolean hasSchedulesByCourseId(Long courseId);
} 