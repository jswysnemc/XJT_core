package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.entity.Course;
import com.ljp.xjt.mapper.CourseMapper;
import com.ljp.xjt.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * 课程服务实现类
 * <p>
 * 实现了 `CourseService` 接口中定义的课程管理业务逻辑。
 * 使用 `CourseMapper` 与数据库进行交互。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    /**
     * 检查课程名称是否已存在。
     * 如果提供了 courseId，则在检查时排除该 ID 对应的课程（用于更新操作）。
     *
     * @param courseName 课程名称
     * @param courseId   当前课程ID (可为null，用于创建时)
     * @return boolean 如果名称已存在（排除自身后），则返回true，否则返回false
     */
    @Override
    public boolean checkCourseNameExists(String courseName, Long courseId) {
        if (!StringUtils.hasText(courseName)) {
            return false; // 名称为空不进行检查
        }
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Course::getCourseName, courseName);
        if (courseId != null) {
            queryWrapper.ne(Course::getId, courseId);
        }
        return baseMapper.exists(queryWrapper);
    }

    /**
     * 检查课程编码是否已存在。
     * 如果提供了 courseId，则在检查时排除该 ID 对应的课程（用于更新操作）。
     *
     * @param courseCode 课程编码
     * @param courseId   当前课程ID (可为null，用于创建时)
     * @return boolean 如果编码已存在（排除自身后），则返回true，否则返回false
     */
    @Override
    public boolean checkCourseCodeExists(String courseCode, Long courseId) {
        if (!StringUtils.hasText(courseCode)) {
            return false; // 编码为空不进行检查
        }
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Course::getCourseCode, courseCode);
        if (courseId != null) {
            queryWrapper.ne(Course::getId, courseId);
        }
        return baseMapper.exists(queryWrapper);
    }

    @Override
    public List<Course> findCoursesByClassId(Long classId) {
        log.info("Finding courses for classId: {}", classId);
        return baseMapper.findCoursesByClassId(classId);
    }
} 