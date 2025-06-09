package com.ljp.xjt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.dto.AdminGradeDto;
import com.ljp.xjt.entity.Grade;
import com.ljp.xjt.entity.TeachingAssignment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 成绩Mapper接口
 * <p>
 * 提供成绩相关的数据访问操作，包括成绩查询、统计分析等核心业务功能
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Mapper
public interface GradeMapper extends BaseMapper<Grade> {

    /**
     * 根据学生ID和课程ID查询成绩
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param semester 学期（可选）
     * @param year 学年（可选）
     * @return 成绩列表
     */
    List<Grade> selectByStudentAndCourse(@Param("studentId") Long studentId,
                                        @Param("courseId") Long courseId,
                                        @Param("semester") String semester,
                                        @Param("year") Integer year);

    /**
     * 根据学生ID查询所有成绩
     *
     * @param studentId 学生ID
     * @param semester 学期（可选）
     * @param year 学年（可选）
     * @return 成绩列表
     */
    List<Grade> selectByStudentId(@Param("studentId") Long studentId,
                                 @Param("semester") String semester,
                                 @Param("year") Integer year);

    /**
     * 分页查询成绩信息（带学生和课程信息）
     *
     * @param page 分页参数
     * @param classId 班级ID（可选）
     * @param courseId 课程ID（可选）
     * @param semester 学期（可选）
     * @param year 学年（可选）
     * @return 成绩分页数据
     */
    IPage<Grade> selectGradesWithDetails(Page<Grade> page,
                                        @Param("classId") Long classId,
                                        @Param("courseId") Long courseId,
                                        @Param("semester") String semester,
                                        @Param("year") Integer year);

    /**
     * 根据教师授课查询成绩列表
     *
     * @param teacherId 教师ID
     * @param courseId 课程ID（可选）
     * @param classId 班级ID（可选）
     * @param semester 学期（可选）
     * @param year 学年（可选）
     * @return 成绩列表
     */
    List<Grade> selectByTeacherCourse(@Param("teacherId") Long teacherId,
                                     @Param("courseId") Long courseId,
                                     @Param("classId") Long classId,
                                     @Param("semester") String semester,
                                     @Param("year") Integer year);

    /**
     * 统计班级课程成绩分布
     *
     * @param classId 班级ID
     * @param courseId 课程ID
     * @param semester 学期
     * @param year 学年
     * @return 成绩统计信息
     */
    List<Map<String, Object>> selectGradeStatistics(@Param("classId") Long classId,
                                     @Param("courseId") Long courseId,
                                     @Param("semester") String semester,
                                     @Param("year") Integer year);

    /**
     * 根据教师ID查询授课关系
     *
     * @param teacherId 教师ID
     * @return 授课关系
     */
    TeachingAssignment findTeachingAssignment(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId, @Param("classId") Long classId);

    /**
     * 根据管理员筛选条件分页查询成绩列表
     *
     * @param page          分页对象
     * @param classId       班级ID
     * @param courseId      课程ID
     * @param studentName   学生姓名 (模糊查询)
     * @param studentNumber 学号 (模糊查询)
     * @return 分页后的成绩列表
     */
    IPage<AdminGradeDto> findGradesByAdminCriteria(
            Page<AdminGradeDto> page,
            @Param("classId") Long classId,
            @Param("courseId") Long courseId,
            @Param("studentName") String studentName,
            @Param("studentNumber") String studentNumber
    );

} 