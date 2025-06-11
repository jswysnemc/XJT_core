package com.ljp.xjt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ljp.xjt.dto.TeachingAssignmentDto;
import com.ljp.xjt.dto.TeachingAssignmentRequestDto;
import com.ljp.xjt.entity.Course;
import com.ljp.xjt.entity.TeachingAssignment;

import java.util.List;

/**
 * 教学分配服务接口
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
public interface TeachingAssignmentService extends IService<TeachingAssignment> {

    /**
     * 分页查询教学分配的详细信息
     *
     * @param page         分页参数
     * @param courseName   课程名称 (可选, 用于筛选)
     * @param teacherName  教师名称 (可选, 用于筛选)
     * @param className    班级名称 (可选, 用于筛选)
     * @return 分页后的教学分配详细信息列表
     */
    IPage<TeachingAssignmentDto> listAssignments(Page<TeachingAssignmentDto> page, String courseName, String teacherName, String className);

    /**
     * 创建一个新的教学分配记录
     *
     * @param requestDto 包含教学分配信息的请求体
     * @return 创建成功后的教学分配实体
     */
    TeachingAssignment createAssignment(TeachingAssignmentRequestDto requestDto);

    /**
     * 更新一个已有的教学分配记录
     *
     * @param id         要更新的教学分配记录ID
     * @param requestDto 包含更新信息的请求体
     * @return 更新成功后的教学分配实体
     */
    TeachingAssignment updateAssignment(Long id, TeachingAssignmentRequestDto requestDto);

    /**
     * 删除一个教学分配记录
     *
     * @param id 要删除的教学分配记录ID
     */
    void deleteAssignment(Long id);

    /**
     * 查询从未被分配过的课程列表
     *
     * @return 未分配的课程列表
     */
    List<Course> findUnassignedCourses();
} 