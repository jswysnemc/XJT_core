package com.ljp.xjt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljp.xjt.entity.TeacherCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教师课程关联Mapper接口
 * <p>
 * 提供教师课程关联的数据访问操作
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Mapper
public interface TeacherCourseMapper extends BaseMapper<TeacherCourse> {

    /**
     * 根据教师ID查询其教授的课程
     *
     * @param teacherId 教师ID
     * @return 课程ID列表
     */
    List<Long> selectCourseIdsByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 根据教师ID和课程ID查询其教授的班级
     *
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @return 班级ID列表
     */
    List<Long> selectClassIdsByTeacherAndCourse(
        @Param("teacherId") Long teacherId, 
        @Param("courseId") Long courseId
    );

} 