package com.ljp.xjt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.dto.StudentGradeDTO;

import java.util.List;

/**
 * 学生服务接口
 * <p>
 * 继承自MyBatis Plus的IService，提供基础的学生管理业务操作。
 * 定义了针对学生模块的业务逻辑方法。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
public interface StudentService extends IService<Student> {

    /**
     * 检查学号是否已存在
     *
     * @param studentNumber 学号
     * @param studentId     当前学生ID (用于更新时排除自身)
     * @return boolean 如果存在则返回true，否则返回false
     */
    boolean checkStudentNumberExists(String studentNumber, Long studentId);

    /**
     * 根据用户ID查询学生信息
     *
     * @param userId 用户ID
     * @return Student 学生信息，如果不存在则返回null
     */
    Student findByUserId(Long userId);

    /**
     * 获取学生信息
     *
     * @param id 学生ID
     * @return 业务异常，如果学生不存在
     */
    Student getStudentById(Long id);

    /**
     * 检查学生是否属于指定班级
     *
     * @param studentId 学生ID
     * @param classId 班级ID
     * @return 如果学生属于该班级，则返回true，否则返回false
     */
    boolean isStudentInClass(Long studentId, Long classId);

    /**
     * 查询当前登录学生的所有成绩
     *
     * @return List<StudentGradeDTO> 包含成绩详情的列表
     */
    List<StudentGradeDTO> findMyGrades();

    // 未来可以添加更多业务方法，例如：
    // Page<StudentDTO> findStudentsWithDetails(Page<Student> page, StudentQueryParam queryParam);
} 