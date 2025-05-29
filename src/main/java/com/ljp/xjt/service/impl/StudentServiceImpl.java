package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.mapper.StudentMapper;
import com.ljp.xjt.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 学生服务实现类
 * <p>
 * 实现了 `StudentService` 接口中定义的学生管理业务逻辑。
 * 使用 `StudentMapper` 与数据库进行交互。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    /**
     * 检查学号是否已存在。
     * 如果提供了 studentId，则在检查时排除该 ID 对应的学生（用于更新操作）。
     *
     * @param studentNumber 学号
     * @param studentId     当前学生ID (可为null，用于创建时)
     * @return boolean 如果学号已存在（排除自身后），则返回true，否则返回false
     */
    @Override
    public boolean checkStudentNumberExists(String studentNumber, Long studentId) {
        if (!StringUtils.hasText(studentNumber)) {
            return false; // 学号为空不进行检查
        }
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getStudentNumber, studentNumber);
        if (studentId != null) {
            queryWrapper.ne(Student::getId, studentId);
        }
        return baseMapper.exists(queryWrapper);
    }

    /**
     * 根据用户ID查询学生信息。
     *
     * @param userId 用户ID
     * @return Student 学生信息，如果不存在则返回null
     */
    @Override
    public Student findByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getUserId, userId);
        return baseMapper.selectOne(queryWrapper);
    }
} 