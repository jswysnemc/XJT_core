package com.ljp.xjt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.dto.TeachingAssignmentDto;
import com.ljp.xjt.entity.Course;
import com.ljp.xjt.entity.TeachingAssignment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学分配表 Mapper 接口
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Mapper
public interface TeachingAssignmentMapper extends BaseMapper<TeachingAssignment> {

    /**
     * 分页查询教学分配的详细信息
     *
     * @param page         分页参数
     * @param courseName   课程名称 (可选, 用于筛选)
     * @param teacherName  教师名称 (可选, 用于筛选)
     * @param className    班级名称 (可选, 用于筛选)
     * @return 分页后的教学分配详细信息列表
     */
    IPage<TeachingAssignmentDto> selectDetailedAssignments(Page<?> page,
                                                         @Param("courseName") String courseName,
                                                         @Param("teacherName") String teacherName,
                                                         @Param("className") String className);

    /**
     * 查询从未被分配过的课程列表
     *
     * @return 未分配的课程列表
     */
    List<Course> selectUnassignedCourses();
} 