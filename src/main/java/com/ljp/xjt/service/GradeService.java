package com.ljp.xjt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ljp.xjt.entity.Grade;

import java.util.List;
import java.util.Map;

/**
 * 成绩服务接口
 * <p>
 * 提供成绩管理相关业务逻辑处理，包括成绩查询、录入、修改、统计等功能
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
public interface GradeService extends IService<Grade> {

    /**
     * 录入成绩
     * 
     * @param grade 成绩信息
     * @param teacherId 教师ID
     * @return 是否成功
     */
    boolean createGrade(Grade grade, Long teacherId);

    /**
     * 批量录入成绩
     * 
     * @param gradeList 成绩列表
     * @param teacherId 教师ID
     * @return 是否成功
     */
    boolean batchCreateGrades(List<Grade> gradeList, Long teacherId);

    /**
     * 修改成绩
     * 
     * @param grade 成绩信息
     * @param teacherId 教师ID
     * @return 是否成功
     */
    boolean updateGrade(Grade grade, Long teacherId);

    /**
     * 批量修改成绩
     * 
     * @param gradeList 成绩列表
     * @param teacherId 教师ID
     * @return 是否成功
     */
    boolean batchUpdateGrades(List<Grade> gradeList, Long teacherId);

    /**
     * 删除成绩
     * 
     * @param id 成绩ID
     * @param teacherId 教师ID
     * @return 是否成功
     */
    boolean deleteGrade(Long id, Long teacherId);

    /**
     * 根据ID查询成绩
     * 
     * @param id 成绩ID
     * @return 成绩信息
     */
    Grade getGradeById(Long id);

    /**
     * 查询学生指定课程的成绩
     * 
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param semester 学期
     * @param year 学年
     * @return 成绩列表
     */
    List<Grade> getStudentCourseGrades(Long studentId, Long courseId, String semester, Integer year);

    /**
     * 查询学生所有成绩
     * 
     * @param studentId 学生ID
     * @param semester 学期
     * @param year 学年
     * @return 成绩列表
     */
    List<Grade> getStudentGrades(Long studentId, String semester, Integer year);

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
    IPage<Grade> getGradeList(Page<Grade> page, Long classId, Long courseId, String semester, Integer year);

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
    List<Grade> getTeacherCourseGrades(Long teacherId, Long courseId, Long classId, String semester, Integer year);

    /**
     * 统计班级课程成绩分布
     * 
     * @param classId 班级ID
     * @param courseId 课程ID
     * @param semester 学期
     * @param year 学年
     * @return 统计结果
     */
    Map<String, Object> getGradeStatistics(Long classId, Long courseId, String semester, Integer year);

    /**
     * 检查教师是否有权限操作该课程成绩
     * 
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param classId 班级ID（可选）
     * @return 是否有权限
     */
    boolean checkTeacherPermission(Long teacherId, Long courseId, Long classId);

    /**
     * 审核成绩（标记或取消标记为异常）
     * 
     * @param id 成绩ID
     * @param isAbnormal 是否异常
     * @param remarks 备注
     * @return 是否成功
     */
    boolean reviewGrade(Long id, Integer isAbnormal, String remarks);
}