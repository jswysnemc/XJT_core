package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.dto.StudentGradeDTO;
import com.ljp.xjt.dto.StudentProfileUpdateDTO;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.mapper.StudentMapper;
import com.ljp.xjt.security.SecurityUser;
import com.ljp.xjt.service.StudentService;
import com.ljp.xjt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

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
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private final UserService userService;

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

    /**
     * 获取学生信息
     *
     * @param id 学生ID
     * @return 业务异常，如果学生不存在
     */
    @Override
    public Student getStudentById(Long id) {
        Student student = baseMapper.selectById(id);
        if (student == null) {
            throw new IllegalArgumentException("ID为 " + id + " 的学生不存在");
        }
        return student;
    }

    /**
     * 检查学生是否属于指定班级
     *
     * @param studentId 学生ID
     * @param classId 班级ID
     * @return 如果学生属于该班级，则返回true，否则返回false
     */
    @Override
    public boolean isStudentInClass(Long studentId, Long classId) {
        if (studentId == null || classId == null) {
            return false;
        }
        Student student = this.getStudentById(studentId);
        return classId.equals(student.getClassId());
    }

    /**
     * 查询当前登录学生的所有成绩
     * <p>
     * 1. 从Spring Security上下文中获取当前登录用户的ID。
     * 2. 根据用户ID查找对应的学生信息。
     * 3. 如果学生存在，则调用mapper查询其所有成绩。
     * 4. 如果学生不存在或未登录，返回空列表。
     * </p>
     *
     * @return List<StudentGradeDTO> 包含成绩详情的列表
     */
    @Override
    public List<StudentGradeDTO> findMyGrades() {
        // 1. 获取当前登录用户的认证信息
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof SecurityUser securityUser) {
            // 2. 根据用户ID查找学生信息
            Long userId = securityUser.getUser().getId();
            Student student = this.findByUserId(userId);

            if (student != null) {
                // 3. 调用mapper查询成绩
                return baseMapper.findGradesByStudentId(student.getId());
            }
        }

        // 4. 如果用户未登录或不是学生，返回空列表
        return Collections.emptyList();
    }

    /**
     * 更新当前登录学生的个人信息
     * <p>
     * 1. 获取当前登录用户及关联的学生信息。
     * 2. 使用DTO中的数据更新User和Student实体。
     * 3. 在一个事务中同时更新users表和students表。
     * </p>
     *
     * @param updateDTO 包含待更新信息的DTO
     * @return boolean 更新是否成功
     */
    @Override
    @Transactional
    public boolean updateMyProfile(StudentProfileUpdateDTO updateDTO) {
        // 1. 获取当前登录用户信息
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof SecurityUser securityUser)) {
            throw new RuntimeException("无法获取当前用户信息");
        }
        User currentUser = securityUser.getUser();
        Student currentStudent = findByUserId(currentUser.getId());

        if (currentStudent == null) {
            throw new RuntimeException("未找到当前用户的学生记录");
        }

        // 2. 更新User实体
        currentUser.setEmail(updateDTO.getEmail());
        currentUser.setPhone(updateDTO.getPhone());
        boolean userUpdated = userService.updateById(currentUser);

        // 3. 更新Student实体
        currentStudent.setStudentName(updateDTO.getStudentName());
        currentStudent.setGender(updateDTO.getGender());
        boolean studentUpdated = this.updateById(currentStudent);

        return userUpdated && studentUpdated;
    }
} 