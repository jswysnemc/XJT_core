package com.ljp.xjt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljp.xjt.entity.Course;
import java.util.List;

/**
 * 课程服务接口
 * <p>
 * 继承自MyBatis Plus的IService，提供基础的课程管理业务操作。
 * 定义了针对课程模块的业务逻辑方法。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
public interface CourseService extends IService<Course> {

    /**
     * 检查课程名称是否已存在
     *
     * @param courseName 课程名称
     * @param courseId   当前课程ID (用于更新时排除自身)
     * @return boolean 如果存在则返回true，否则返回false
     */
    boolean checkCourseNameExists(String courseName, Long courseId);

    /**
     * 检查课程编码是否已存在
     *
     * @param courseCode 课程编码
     * @param courseId   当前课程ID (用于更新时排除自身)
     * @return boolean 如果存在则返回true，否则返回false
     */
    boolean checkCourseCodeExists(String courseCode, Long courseId);

    /**
     * 根据班级ID查询该班级的所有课程
     *
     * @param classId 班级ID
     * @return 课程列表
     */
    List<Course> findCoursesByClassId(Long classId);

    // 未来可以添加更多业务方法，例如：
    // List<Course> findCoursesByTeacherId(Long teacherId);
} 