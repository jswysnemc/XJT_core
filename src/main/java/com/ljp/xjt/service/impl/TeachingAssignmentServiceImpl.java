package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.dto.TeachingAssignmentDto;
import com.ljp.xjt.dto.TeachingAssignmentRequestDto;
import com.ljp.xjt.entity.Course;
import com.ljp.xjt.entity.TeachingAssignment;
import com.ljp.xjt.mapper.TeachingAssignmentMapper;
import com.ljp.xjt.service.TeachingAssignmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 教学分配服务实现类
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Service
@Slf4j
public class TeachingAssignmentServiceImpl extends ServiceImpl<TeachingAssignmentMapper, TeachingAssignment> implements TeachingAssignmentService {

    @Override
    public IPage<TeachingAssignmentDto> listAssignments(Page<TeachingAssignmentDto> page, String courseName, String teacherName, String className) {
        return baseMapper.selectDetailedAssignments(page, courseName, teacherName, className);
    }

    @Override
    @Transactional
    public TeachingAssignment createAssignment(TeachingAssignmentRequestDto requestDto) {
        // 1. 检查是否存在相同的排课记录 (唯一性约束)
        if (checkIfExist(requestDto.getTeacherId(), requestDto.getCourseId(), requestDto.getClassId(), requestDto.getSemester(), requestDto.getYear(), null)) {
            throw new IllegalArgumentException("已存在相同的排课记录");
        }

        // 2. 创建实体并保存
        TeachingAssignment teachingAssignment = new TeachingAssignment();
        BeanUtils.copyProperties(requestDto, teachingAssignment);
        this.save(teachingAssignment);
        log.info("Created new teaching assignment with id: {}", teachingAssignment.getId());
        return teachingAssignment;
    }

    @Override
    @Transactional
    public TeachingAssignment updateAssignment(Long id, TeachingAssignmentRequestDto requestDto) {
        // 1. 检查待更新的记录是否存在
        TeachingAssignment existingAssignment = this.getById(id);
        if (existingAssignment == null) {
            throw new IllegalArgumentException("找不到ID为 " + id + " 的排课记录");
        }
        
        // 2. 检查更新后的记录是否与其它记录冲突 (唯一性约束)
        if (checkIfExist(requestDto.getTeacherId(), requestDto.getCourseId(), requestDto.getClassId(), requestDto.getSemester(), requestDto.getYear(), id)) {
            throw new IllegalArgumentException("更新后的排课记录与现有记录冲突");
        }

        // 3. 更新实体并保存
        BeanUtils.copyProperties(requestDto, existingAssignment);
        this.updateById(existingAssignment);
        log.info("Updated teaching assignment with id: {}", id);
        return existingAssignment;
    }

    @Override
    public void deleteAssignment(Long id) {
        if (!this.removeById(id)) {
            throw new IllegalArgumentException("找不到ID为 " + id + " 的排课记录，无法删除");
        }
        log.info("Deleted teaching assignment with id: {}", id);
    }

    @Override
    public List<Course> findUnassignedCourses() {
        return this.baseMapper.selectUnassignedCourses();
    }
    
    /**
     * 检查是否存在符合条件的排课记录 (用于保证唯一性)
     *
     * @param teacherId  教师ID
     * @param courseId   课程ID
     * @param classId    班级ID
     * @param semester   学期
     * @param year       学年
     * @param excludeId  要排除的ID (用于更新检查，可为null)
     * @return 如果存在则返回true，否则返回false
     */
    private boolean checkIfExist(Long teacherId, Long courseId, Long classId, String semester, Integer year, Long excludeId) {
        QueryWrapper<TeachingAssignment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacherId)
                    .eq("course_id", courseId)
                    .eq("class_id", classId)
                    .eq("semester", semester)
                    .eq("year", year);
        
        if (excludeId != null) {
            queryWrapper.ne("id", excludeId);
        }
        
        return this.baseMapper.selectCount(queryWrapper) > 0;
    }
} 