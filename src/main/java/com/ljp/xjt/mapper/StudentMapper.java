package com.ljp.xjt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生数据访问层接口
 * <p>
 * 继承自MyBatis Plus的BaseMapper，提供基础的CRUD操作。
 * 包含对 `students` 表的数据库操作方法。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 根据用户ID查询学生信息
     *
     * @param userId 用户ID
     * @return 学生信息
     */
    Student selectByUserId(@Param("userId") Long userId);

    /**
     * 根据学号查询学生信息
     *
     * @param studentNumber 学号
     * @return 学生信息
     */
    Student selectByStudentNumber(@Param("studentNumber") String studentNumber);

    /**
     * 根据班级ID查询学生列表
     *
     * @param classId 班级ID
     * @return 学生列表
     */
    List<Student> selectByClassId(@Param("classId") Long classId);

    /**
     * 分页查询学生信息（带班级信息）
     *
     * @param page 分页参数
     * @param classId 班级ID（可选）
     * @param studentName 学生姓名（可选，模糊查询）
     * @return 学生分页数据
     */
    IPage<Student> selectStudentsWithClass(Page<Student> page, 
                                          @Param("classId") Long classId,
                                          @Param("studentName") String studentName);

    /**
     * 分页查询学生列表
     *
     * @param page 分页参数
     * @param studentName 学生姓名（可选，模糊查询）
     * @param classId 班级ID（可选）
     * @return {@link IPage< Student>}
     */
    IPage<Student> selectStudentList(Page<Student> page, @Param("studentName") String studentName, @Param("classId") Long classId);

    /**
     * 根据学号和班级ID查找学生
     *
     * @param studentNumber 学号
     * @param classId 班级ID
     * @return 学生实体
     */
    Student findStudentByNumberAndClassId(@Param("studentNumber") String studentNumber, @Param("classId") Long classId);

    // 如果需要自定义SQL查询，可以在这里添加方法声明
    // 例如：根据学号查询学生信息
    // Student findByStudentNumber(@Param("studentNumber") String studentNumber);

} 