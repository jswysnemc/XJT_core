package com.ljp.xjt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljp.xjt.entity.Student;

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

    // 未来可以添加更多业务方法，例如：
    // Page<StudentDTO> findStudentsWithDetails(Page<Student> page, StudentQueryParam queryParam);
} 