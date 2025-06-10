package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.dto.AdminGradeDto;
import com.ljp.xjt.dto.AdminGradeUpdateRequestDto;
import com.ljp.xjt.entity.Grade;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.entity.TeachingAssignment;
import com.ljp.xjt.mapper.GradeMapper;
import com.ljp.xjt.mapper.StudentMapper;
import com.ljp.xjt.mapper.TeachingAssignmentMapper;
import com.ljp.xjt.service.GradeService;
import com.ljp.xjt.utils.GpaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

    private final StudentMapper studentMapper;
    private final TeachingAssignmentMapper teachingAssignmentMapper;
    private final GradeMapper gradeMapper;

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
        
        // 1. 检查学生是否存在
        Student student = studentMapper.selectById(grade.getStudentId());
        if (student == null) {
            log.error("Student not found: {}", grade.getStudentId());
            throw new IllegalArgumentException("学生不存在");
        }
        
        // 2. 检查是否已存在该学生该课程的同类型成绩
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
        
        // 3. 设置创建者
        grade.setCreatedBy(teacherId);
        grade.setIsAbnormal(0); // 默认为正常状态
        
        // 4. 保存成绩
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
        
        // 2. 如果成绩被标记为异常，普通教师不能修改
        if (existingGrade.getIsAbnormal() == 1) {
            log.error("Grade {} is marked as abnormal, cannot be updated by teacher", grade.getId());
            throw new IllegalArgumentException("该成绩已被标记为异常，请联系管理员");
        }
        
        // 3. 只允许修改分数、类型、学期、学年和备注
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
        
        // 4. 更新成绩
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
        
        // 1. 获取原成绩信息
        Grade existingGrade = this.getById(id);
        if (existingGrade == null) {
            return true; // 幂等性，如果不存在，也算删除成功
        }
        
        // 2. 如果成绩被标记为异常，普通教师不能删除
        if (existingGrade.getIsAbnormal() == 1) {
            log.error("Grade {} is marked as abnormal, cannot be deleted by teacher", id);
            throw new IllegalArgumentException("该成绩已被标记为异常，无法删除");
        }
        
        // 3. 删除成绩
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
        log.info("Getting grades for student {}, course {}, semester {}, year {}", studentId, courseId, semester, year);
        LambdaQueryWrapper<Grade> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Grade::getStudentId, studentId)
                .eq(Grade::getCourseId, courseId);
        if (semester != null) {
            queryWrapper.eq(Grade::getSemester, semester);
        }
        if (year != null) {
            queryWrapper.eq(Grade::getYear, year);
        }
        return baseMapper.selectList(queryWrapper);
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
     * 分页查询成绩列表
     *
     * @param page      分页对象
     * @param classId   班级ID
     * @param courseId  课程ID
     * @param semester  学期
     * @param year      学年
     * @return 分页后的成绩列表
     */
    @Override
    public IPage<Grade> getGradeList(Page<Grade> page, Long classId, Long courseId, String semester, Integer year) {
        log.info("Getting grade list with pagination - class: {}, course: {}, semester: {}, year: {}",
                classId, courseId, semester, year);
        LambdaQueryWrapper<Grade> queryWrapper = new LambdaQueryWrapper<>();
         // 这里应该基于班级和课程来查询，而不是学生ID
        if (classId != null) {
            // 需要通过classId查询所有学生ID，然后作为查询条件
            // List<Long> studentIds = studentMapper.selectStudentIdsByClassId(classId);
            // queryWrapper.in(Grade::getStudentId, studentIds);
        }
        queryWrapper.eq(courseId != null, Grade::getCourseId, courseId);
        if (semester != null) {
            queryWrapper.eq(Grade::getSemester, semester);
        }
        if (year != null) {
            queryWrapper.eq(Grade::getYear, year);
        }
        return baseMapper.selectPage(page, queryWrapper);
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
        log.info("Getting grades for teacher: {}, course: {}, class: {}", teacherId, courseId, classId);
        return baseMapper.selectByTeacherCourse(teacherId, courseId, classId, semester, year);
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
        log.info("Reviewing grade: {}, setting isAbnormal to {}", id, isAbnormal);
        
        // 1. 获取原成绩信息
        Grade existingGrade = this.getById(id);
        if (existingGrade == null) {
            log.error("Grade not found: {}", id);
            throw new IllegalArgumentException("成绩不存在");
        }

        // 2. 更新审核状态和备注
        LambdaUpdateWrapper<Grade> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Grade::getId, id)
                     .set(Grade::getIsAbnormal, isAbnormal)
                     .set(Grade::getRemarks, remarks);
        
        return this.update(updateWrapper);
    }

    @Override
    public TeachingAssignment verifyAndGetTeachingAssignment(Long teacherId, Long courseId, Long classId) {
        LambdaQueryWrapper<TeachingAssignment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachingAssignment::getTeacherId, teacherId)
                    .eq(TeachingAssignment::getCourseId, courseId)
                    .eq(TeachingAssignment::getClassId, classId);
        
        TeachingAssignment assignment = teachingAssignmentMapper.selectOne(queryWrapper);
        if (assignment == null) {
            throw new SecurityException("无权操作，该教师未被指派教授此班级的该门课程");
        }
        return assignment;
    }

    /**
     * 更新或插入一条成绩记录
     *
     * @param studentId 学生ID
     * @param courseId  课程ID
     * @param score     分数
     * @param teacherId 操作的教师ID
     * @param semester  学期
     * @param year      学年
     * @return 操作是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean upsertGrade(Long studentId, Long courseId, BigDecimal score, Long teacherId, String semester, Integer year) {
        log.info("Upserting grade for student {}, course {}", studentId, courseId);

        // 1. 查找是否已存在该学生该课程的成绩记录
        LambdaQueryWrapper<Grade> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Grade::getStudentId, studentId)
                    .eq(Grade::getCourseId, courseId);
        
        Grade existingGrade = this.getOne(queryWrapper);

        if (existingGrade != null) {
            // 2a. 如果存在，则更新
            log.info("Grade found (id: {}), updating score to {}", existingGrade.getId(), score);
            existingGrade.setScore(score);
            existingGrade.setIsAbnormal(0); // 每次教师修改都重置为正常状态
            existingGrade.setSemester(semester);
            existingGrade.setYear(year);
            return this.updateById(existingGrade);
        } else {
            // 2b. 如果不存在，则插入新记录
            log.info("Grade not found, creating new one with score {}", score);
            Grade newGrade = new Grade();
            newGrade.setStudentId(studentId);
            newGrade.setCourseId(courseId);
            newGrade.setScore(score);
            newGrade.setCreatedBy(teacherId);
            newGrade.setIsAbnormal(0); // 默认为正常状态
            newGrade.setSemester(semester);
            newGrade.setYear(year);
            // 注意：gradeType字段可能需要根据业务逻辑设置默认值或从其他地方获取
            // 这里暂时不设置
            return this.save(newGrade);
        }
    }

    @Override
    public IPage<AdminGradeDto> getGradesByAdminCriteria(
            Page<AdminGradeDto> page,
            Long classId,
            Long courseId,
            String studentName,
            String studentNumber) {
        log.info("Admin querying grades with criteria - classId: {}, courseId: {}, studentName: '{}', studentNumber: '{}'",
                classId, courseId, studentName, studentNumber);
        IPage<AdminGradeDto> gradePage = gradeMapper.getGradesByAdminCriteria(page, classId, courseId, studentName, studentNumber);

        // 对查询结果进行二次处理，计算GPA和判断成绩是否正常
        gradePage.getRecords().forEach(grade -> {
            grade.setGpa(GpaUtil.calculateGpa(grade.getScore()));
            grade.setNormal(GpaUtil.isScoreNormal(grade.getScore()));
        });
        
        return gradePage;
    }

    @Override
    @Transactional
    public boolean adminUpdateGrade(Long gradeId, AdminGradeUpdateRequestDto updateDto) {
        // 1. 根据ID查找成绩记录
        Grade grade = this.getById(gradeId);
        if (grade == null) {
            log.warn("Attempted to update a non-existent grade with ID: {}", gradeId);
            throw new IllegalArgumentException("要修改的成绩记录不存在");
        }

        // 2. 使用DTO更新成绩实体
        grade.setScore(updateDto.getScore());
        grade.setReviewed(updateDto.getIsReviewed());
        
        // 注意：管理员修改成绩时，不更新 `updated_by_teacher_id`

        // 3. 保存更新
        return this.updateById(grade);
    }

    /**
     * 根据分数计算绩点 (GPA)
     * @param score 分数
     * @return 绩点
     */
    private BigDecimal calculateGpa(BigDecimal score) {
        if (score == null) {
            return BigDecimal.ZERO;
        }
        if (score.compareTo(new BigDecimal("90")) >= 0) {
            return new BigDecimal("4.0");
        } else if (score.compareTo(new BigDecimal("80")) >= 0) {
            return new BigDecimal("3.0");
        } else if (score.compareTo(new BigDecimal("70")) >= 0) {
            return new BigDecimal("2.0");
        } else if (score.compareTo(new BigDecimal("60")) >= 0) {
            return new BigDecimal("1.0");
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 判断成绩是否及格（正常）
     * @param score 分数
     * @return 是否及格
     */
    private boolean isScoreNormal(BigDecimal score) {
        if (score == null) {
            return false;
        }
        return score.compareTo(new BigDecimal("60")) >= 0;
    }
}