package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.entity.Grade;
import com.ljp.xjt.entity.TeacherCourse;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.mapper.GradeMapper;
import com.ljp.xjt.mapper.TeacherCourseMapper;
import com.ljp.xjt.mapper.StudentMapper;
import com.ljp.xjt.service.GradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩服务实现类
 * <p>
 * 实现成绩管理相关业务逻辑处理，包括成绩查询、录入、修改、统计等功能
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {

    private final TeacherCourseMapper teacherCourseMapper;
    private final StudentMapper studentMapper;

    /**
     * 录入成绩
     *
     * @param grade 成绩信息
     * @param teacherId 教师ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createGrade(Grade grade, Long teacherId) {
        log.info("Creating grade for student {} and course {}", grade.getStudentId(), grade.getCourseId());
        
        // 1. 验证教师是否有权限操作该课程
        if (!checkTeacherPermission(teacherId, grade.getCourseId(), null)) {
            log.error("Teacher {} has no permission to create grade for course {}", teacherId, grade.getCourseId());
            throw new IllegalArgumentException("无权为该课程录入成绩");
        }
        
        // 2. 检查学生是否存在
        Student student = studentMapper.selectById(grade.getStudentId());
        if (student == null) {
            log.error("Student not found: {}", grade.getStudentId());
            throw new IllegalArgumentException("学生不存在");
        }
        
        // 3. 检查是否已存在该学生该课程的同类型成绩
        LambdaQueryWrapper<Grade> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Grade::getStudentId, grade.getStudentId())
                  .eq(Grade::getCourseId, grade.getCourseId())
                  .eq(Grade::getGradeType, grade.getGradeType())
                  .eq(Grade::getSemester, grade.getSemester())
                  .eq(Grade::getYear, grade.getYear());
        
        long count = this.count(queryWrapper);
        if (count > 0) {
            log.error("Grade already exists for student {} and course {}", grade.getStudentId(), grade.getCourseId());
            throw new IllegalArgumentException("该学生该课程的成绩已存在");
        }
        
        // 4. 设置创建者
        grade.setCreatedBy(teacherId);
        grade.setIsAbnormal(0); // 默认为正常状态
        
        // 5. 保存成绩
        return this.save(grade);
    }

    /**
     * 批量录入成绩
     *
     * @param gradeList 成绩列表
     * @param teacherId 教师ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreateGrades(List<Grade> gradeList, Long teacherId) {
        if (CollectionUtils.isEmpty(gradeList)) {
            return true;
        }
        
        log.info("Batch creating grades, count: {}", gradeList.size());
        
        // 验证教师是否有权限操作
        Long courseId = gradeList.get(0).getCourseId();
        if (!checkTeacherPermission(teacherId, courseId, null)) {
            log.error("Teacher {} has no permission to create grades for course {}", teacherId, courseId);
            throw new IllegalArgumentException("无权为该课程录入成绩");
        }
        
        // 验证学生存在性并检查重复成绩
        for (Grade grade : gradeList) {
            // 检查学生是否存在
            Student student = studentMapper.selectById(grade.getStudentId());
            if (student == null) {
                log.error("Student not found: {}", grade.getStudentId());
                throw new IllegalArgumentException("学生ID不存在: " + grade.getStudentId());
            }
            
            // 检查是否已存在该学生该课程的同类型成绩
            LambdaQueryWrapper<Grade> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Grade::getStudentId, grade.getStudentId())
                      .eq(Grade::getCourseId, grade.getCourseId())
                      .eq(Grade::getGradeType, grade.getGradeType())
                      .eq(Grade::getSemester, grade.getSemester())
                      .eq(Grade::getYear, grade.getYear());
            
            long count = this.count(queryWrapper);
            if (count > 0) {
                log.error("Grade already exists for student {} and course {}", grade.getStudentId(), grade.getCourseId());
                throw new IllegalArgumentException("学生ID: " + grade.getStudentId() + " 的成绩已存在");
            }
            
            // 设置创建者和正常状态
            grade.setCreatedBy(teacherId);
            grade.setIsAbnormal(0);
        }
        
        // 批量保存
        return this.saveBatch(gradeList);
    }

    /**
     * 修改成绩
     *
     * @param grade 成绩信息
     * @param teacherId 教师ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateGrade(Grade grade, Long teacherId) {
        log.info("Updating grade: {}", grade.getId());
        
        // 1. 获取原成绩信息
        Grade existingGrade = this.getById(grade.getId());
        if (existingGrade == null) {
            log.error("Grade not found: {}", grade.getId());
            throw new IllegalArgumentException("成绩不存在");
        }
        
        // 2. 验证教师是否有权限操作该课程
        if (!checkTeacherPermission(teacherId, existingGrade.getCourseId(), null)) {
            log.error("Teacher {} has no permission to update grade {}", teacherId, grade.getId());
            throw new IllegalArgumentException("无权修改该成绩");
        }
        
        // 3. 如果成绩被标记为异常，普通教师不能修改
        if (existingGrade.getIsAbnormal() == 1) {
            log.error("Grade {} is marked as abnormal, cannot be updated by teacher", grade.getId());
            throw new IllegalArgumentException("该成绩已被标记为异常，请联系管理员");
        }
        
        // 4. 只允许修改分数、类型、学期、学年和备注
        existingGrade.setScore(grade.getScore());
        if (grade.getGradeType() != null) {
            existingGrade.setGradeType(grade.getGradeType());
        }
        if (grade.getSemester() != null) {
            existingGrade.setSemester(grade.getSemester());
        }
        if (grade.getYear() != null) {
            existingGrade.setYear(grade.getYear());
        }
        if (grade.getRemarks() != null) {
            existingGrade.setRemarks(grade.getRemarks());
        }
        
        // 5. 更新成绩
        return this.updateById(existingGrade);
    }

    /**
     * 批量修改成绩
     *
     * @param gradeList 成绩列表
     * @param teacherId 教师ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateGrades(List<Grade> gradeList, Long teacherId) {
        if (CollectionUtils.isEmpty(gradeList)) {
            return true;
        }
        
        log.info("Batch updating grades, count: {}", gradeList.size());
        
        List<Grade> updatedGradeList = new ArrayList<>();
        
        for (Grade grade : gradeList) {
            // 获取原成绩信息
            Grade existingGrade = this.getById(grade.getId());
            if (existingGrade == null) {
                log.error("Grade not found: {}", grade.getId());
                throw new IllegalArgumentException("成绩ID不存在: " + grade.getId());
            }
            
            // 验证教师是否有权限操作该课程
            if (!checkTeacherPermission(teacherId, existingGrade.getCourseId(), null)) {
                log.error("Teacher {} has no permission to update grade {}", teacherId, grade.getId());
                throw new IllegalArgumentException("无权修改成绩ID: " + grade.getId());
            }
            
            // 如果成绩被标记为异常，普通教师不能修改
            if (existingGrade.getIsAbnormal() == 1) {
                log.error("Grade {} is marked as abnormal, cannot be updated by teacher", grade.getId());
                throw new IllegalArgumentException("成绩ID: " + grade.getId() + " 已被标记为异常，请联系管理员");
            }
            
            // 只允许修改分数、类型、学期、学年和备注
            existingGrade.setScore(grade.getScore());
            if (grade.getGradeType() != null) {
                existingGrade.setGradeType(grade.getGradeType());
            }
            if (grade.getSemester() != null) {
                existingGrade.setSemester(grade.getSemester());
            }
            if (grade.getYear() != null) {
                existingGrade.setYear(grade.getYear());
            }
            if (grade.getRemarks() != null) {
                existingGrade.setRemarks(grade.getRemarks());
            }
            
            updatedGradeList.add(existingGrade);
        }
        
        // 批量更新
        return this.updateBatchById(updatedGradeList);
    }

    /**
     * 删除成绩
     *
     * @param id 成绩ID
     * @param teacherId 教师ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteGrade(Long id, Long teacherId) {
        log.info("Deleting grade: {}", id);
        
        // 1. 获取成绩信息
        Grade grade = this.getById(id);
        if (grade == null) {
            log.error("Grade not found: {}", id);
            throw new IllegalArgumentException("成绩不存在");
        }
        
        // 2. 验证教师是否有权限操作该课程
        if (!checkTeacherPermission(teacherId, grade.getCourseId(), null)) {
            log.error("Teacher {} has no permission to delete grade {}", teacherId, id);
            throw new IllegalArgumentException("无权删除该成绩");
        }
        
        // 3. 如果成绩被标记为异常，普通教师不能删除
        if (grade.getIsAbnormal() == 1) {
            log.error("Grade {} is marked as abnormal, cannot be deleted by teacher", id);
            throw new IllegalArgumentException("该成绩已被标记为异常，请联系管理员");
        }
        
        // 4. 删除成绩
        return this.removeById(id);
    }

    /**
     * 根据ID查询成绩
     *
     * @param id 成绩ID
     * @return 成绩信息
     */
    @Override
    public Grade getGradeById(Long id) {
        log.info("Getting grade by id: {}", id);
        return this.getById(id);
    }

    /**
     * 查询学生指定课程的成绩
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param semester 学期
     * @param year 学年
     * @return 成绩列表
     */
    @Override
    public List<Grade> getStudentCourseGrades(Long studentId, Long courseId, String semester, Integer year) {
        log.info("Getting grades for student {} and course {}, semester: {}, year: {}", 
                 studentId, courseId, semester, year);
        return this.baseMapper.selectByStudentAndCourse(studentId, courseId, semester, year);
    }

    /**
     * 查询学生所有成绩
     *
     * @param studentId 学生ID
     * @param semester 学期
     * @param year 学年
     * @return 成绩列表
     */
    @Override
    public List<Grade> getStudentGrades(Long studentId, String semester, Integer year) {
        log.info("Getting grades for student {}, semester: {}, year: {}", studentId, semester, year);
        return this.baseMapper.selectByStudentId(studentId, semester, year);
    }

    /**
     * 分页查询成绩列表(带详细信息)
     *
     * @param page 分页参数
     * @param classId 班级ID
     * @param courseId 课程ID
     * @param semester 学期
     * @param year 学年
     * @return 成绩分页数据
     */
    @Override
    public IPage<Grade> getGradeList(Page<Grade> page, Long classId, Long courseId, String semester, Integer year) {
        log.info("Getting grade list with classId: {}, courseId: {}, semester: {}, year: {}", 
                 classId, courseId, semester, year);
        return this.baseMapper.selectGradesWithDetails(page, classId, courseId, semester, year);
    }

    /**
     * 教师查询教授课程的学生成绩
     *
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param classId 班级ID
     * @param semester 学期
     * @param year 学年
     * @return 成绩列表
     */
    @Override
    public List<Grade> getTeacherCourseGrades(Long teacherId, Long courseId, Long classId, String semester, Integer year) {
        log.info("Getting grades for teacher {} and course {}, classId: {}, semester: {}, year: {}", 
                 teacherId, courseId, classId, semester, year);
        return this.baseMapper.selectByTeacherCourse(teacherId, courseId, classId, semester, year);
    }

    /**
     * 统计班级课程成绩分布
     *
     * @param classId 班级ID
     * @param courseId 课程ID
     * @param semester 学期
     * @param year 学年
     * @return 统计结果
     */
    @Override
    public Map<String, Object> getGradeStatistics(Long classId, Long courseId, String semester, Integer year) {
        log.info("Getting grade statistics for classId: {}, courseId: {}, semester: {}, year: {}", 
                 classId, courseId, semester, year);
        
        // 查询统计结果
        List<Map<String, Object>> statisticsResult = this.baseMapper.selectGradeStatistics(classId, courseId, semester, year);
        
        Map<String, Object> statistics = new HashMap<>();
        if (!CollectionUtils.isEmpty(statisticsResult)) {
            statistics = statisticsResult.get(0);
        } else {
            // 如果没有查询到数据，提供默认值
            statistics.put("total_count", 0);
            statistics.put("avg_score", 0.0);
            statistics.put("max_score", 0.0);
            statistics.put("min_score", 0.0);
            statistics.put("excellent_count", 0);
            statistics.put("good_count", 0);
            statistics.put("average_count", 0);
            statistics.put("pass_count", 0);
            statistics.put("fail_count", 0);
        }
        
        return statistics;
    }

    /**
     * 检查教师是否有权限操作该课程成绩
     *
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param classId 班级ID（可选）
     * @return 是否有权限
     */
    @Override
    public boolean checkTeacherPermission(Long teacherId, Long courseId, Long classId) {
        log.info("Checking teacher permission: teacherId={}, courseId={}, classId={}", 
                teacherId, courseId, classId);
        
        // 查询教师是否教授该课程
        LambdaQueryWrapper<TeacherCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherCourse::getTeacherId, teacherId)
               .eq(TeacherCourse::getCourseId, courseId);
        
        if (classId != null) {
            wrapper.eq(TeacherCourse::getClassId, classId);
        }
        
        Long count = teacherCourseMapper.selectCount(wrapper);
        
        boolean hasPermission = count > 0;
        log.info("Teacher permission check result: {}", hasPermission);
        
        return hasPermission;
    }

    /**
     * 审核成绩（标记或取消标记为异常）
     *
     * @param id 成绩ID
     * @param isAbnormal 是否异常
     * @param remarks 备注
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewGrade(Long id, Integer isAbnormal, String remarks) {
        log.info("Reviewing grade: {}, isAbnormal: {}", id, isAbnormal);
        
        // 1. 获取成绩信息
        Grade grade = this.getById(id);
        if (grade == null) {
            log.error("Grade not found: {}", id);
            throw new IllegalArgumentException("成绩不存在");
        }
        
        // 2. 更新异常状态和备注
        LambdaUpdateWrapper<Grade> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Grade::getId, id)
                   .set(Grade::getIsAbnormal, isAbnormal)
                   .set(remarks != null, Grade::getRemarks, remarks);
        
        return this.update(updateWrapper);
    }
}