package com.ljp.xjt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.entity.Teacher;
import com.ljp.xjt.dto.TeacherClassDto;
import com.ljp.xjt.dto.TeacherCourseDto;
import com.ljp.xjt.dto.StudentDto;
import com.ljp.xjt.dto.TeacherProfileDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 教师数据访问接口
 * <p>
 * 提供对teachers表的CRUD操作和自定义查询方法
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    /**
     * 分页查询教师列表
     *
     * @param page 分页参数
     * @param teacherName 教师姓名(模糊查询)
     * @param departmentId 部门ID
     * @return 教师分页列表
     */
    IPage<Teacher> selectTeacherList(Page<Teacher> page, 
                                   @Param("teacherName") String teacherName,
                                   @Param("departmentId") Long departmentId);
    
    /**
     * 根据用户ID查询教师信息
     *
     * @param userId 用户ID
     * @return 教师信息
     */
    Teacher selectByUserId(@Param("userId") Long userId);
    
    /**
     * 检查教工号是否存在
     *
     * @param teacherNumber 教工号
     * @return 存在数量
     */
    int checkTeacherNumberExists(@Param("teacherNumber") String teacherNumber);

    /**
     * 根据用户ID查询该教师所教授的课程列表
     *
     * @param userId 用户ID
     * @return 教师课程列表
     */
    List<TeacherCourseDto> findCoursesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和课程ID，查询该教师在该课程下所教授的班级列表
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 教师班级列表
     */
    List<TeacherClassDto> findClassesByCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 根据用户ID、班级ID和课程ID，查询学生列表
     *
     * @param userId   用户ID
     * @param classId  班级ID
     * @param courseId 课程ID
     * @return 学生列表
     */
    List<StudentDto> findStudentsByClassAndCourse(@Param("userId") Long userId, @Param("classId") Long classId, @Param("courseId") Long courseId);

    /**
     * 根据用户ID查询教师的详细个人资料
     *
     * @param userId 用户ID
     * @return 教师个人资料DTO
     */
    TeacherProfileDto findTeacherProfileByUserId(@Param("userId") Long userId);

    /**
     * 根据教师ID统计其教授的课程总数
     *
     * @param teacherId 教师ID
     * @return 课程总数
     */
    Long countTaughtCourses(@Param("teacherId") Long teacherId);

    /**
     * 根据教师ID统计其教授的班级总数
     *
     * @param teacherId 教师ID
     * @return 班级总数
     */
    Long countTaughtClasses(@Param("teacherId") Long teacherId);

    /**
     * 根据教师ID统计其教授的学生总数
     *
     * @param teacherId 教师ID
     * @return 学生总数
     */
    Long countTaughtStudents(@Param("teacherId") Long teacherId);

    /**
     * 根据教师ID计算其所有学生的平均分
     *
     * @param teacherId 教师ID
     * @return 平均分
     */
    BigDecimal calculateAverageScore(@Param("teacherId") Long teacherId);
} 