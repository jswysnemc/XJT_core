package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.common.exception.BusinessException;
import com.ljp.xjt.dto.*;
import com.ljp.xjt.entity.Classes;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.mapper.ClassesMapper;
import com.ljp.xjt.mapper.StudentMapper;
import com.ljp.xjt.mapper.UserMapper;
import com.ljp.xjt.security.SecurityUser;
import com.ljp.xjt.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 学生服务实现类
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private final StudentMapper studentMapper;
    private final UserMapper userMapper;
    private final ClassesMapper classesMapper;

    @Override
    public IPage<Student> list(Page<Student> page, String studentName) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(studentName), Student::getStudentName, studentName);
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<StudentDTO> selectStudentPage(Page<Student> page, LambdaQueryWrapper<Student> queryWrapper) {
        // 1. 分页查询基础学生数据
        Page<Student> studentPage = baseMapper.selectPage(page, queryWrapper);
        List<Student> studentRecords = studentPage.getRecords();

        if (CollectionUtils.isEmpty(studentRecords)) {
            return new Page<>();
        }

        // 2. 获取班级ID列表
        Set<Long> classIds = studentRecords.stream()
                .map(Student::getClassId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        // 3. 一次性查询所有涉及的班级信息
        Map<Long, String> classIdToNameMap = Collections.emptyMap();
        if (!CollectionUtils.isEmpty(classIds)) {
            List<Classes> classesList = classesMapper.selectBatchIds(classIds);
            classIdToNameMap = classesList.stream()
                    .collect(Collectors.toMap(Classes::getId, Classes::getClassName));
        }

        // 4. 转换为DTO列表
        final Map<Long, String> finalClassIdToNameMap = classIdToNameMap;
        List<StudentDTO> dtoList = studentRecords.stream().map(student -> {
            StudentDTO dto = new StudentDTO();
            BeanUtils.copyProperties(student, dto);
            if (student.getClassId() != null) {
                dto.setClassName(finalClassIdToNameMap.get(student.getClassId()));
            }
            return dto;
        }).collect(Collectors.toList());

        // 5. 创建并返回DTO分页结果
        Page<StudentDTO> dtoPage = new Page<>(studentPage.getCurrent(), studentPage.getSize(), studentPage.getTotal());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public boolean saveStudent(Student student) {
        return save(student);
    }

    @Override
    public boolean updateStudent(Student student) {
        return updateById(student);
    }

    @Override
    public StudentProfileDTO getStudentProfileByUserId(Long userId) {
        Student student = findByUserId(userId);
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户信息不存在");
        }
        StudentProfileDTO dto = new StudentProfileDTO();
        BeanUtils.copyProperties(student, dto);
        BeanUtils.copyProperties(user, dto);
        dto.setId(student.getId());
        return dto;
    }

    @Override
    @Transactional
    public void updateStudentProfile(Long userId, StudentProfileUpdateDTO dto) {
        Student student = findByUserId(userId);
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }
        student.setStudentName(dto.getStudentName());
        student.setGender(dto.getGender());
        updateById(student);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户信息不存在");
        }
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        userMapper.updateById(user);
    }

    @Override
    public List<StudentCourseDTO> getStudentCourses(Long userId) {
        Student student = findByUserId(userId);
        return student != null ? studentMapper.findCoursesByStudentId(student.getId()) : Collections.emptyList();
    }

    @Override
    public List<StudentGradeDTO> getStudentGrades(Long userId) {
        Student student = findByUserId(userId);
        return student != null ? studentMapper.findGradesByStudentId(student.getId()) : Collections.emptyList();
    }

    @Override
    public StudentDetailDTO getStudentDetailById(Long studentId) {
        return studentMapper.findStudentDetailById(studentId);
    }

    @Override
    public boolean checkStudentNumberExists(String studentNumber, Long studentId) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getStudentNumber, studentNumber);
        if (studentId != null) {
            queryWrapper.ne(Student::getId, studentId);
        }
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public Student findByUserId(Long userId) {
        return this.getOne(new LambdaQueryWrapper<Student>().eq(Student::getUserId, userId));
    }

    @Override
    public Student getStudentById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public boolean isStudentInClass(Long studentId, Long classId) {
        if (studentId == null || classId == null) return false;
        Student student = getStudentById(studentId);
        return classId.equals(student.getClassId());
    }

    @Override
    public List<StudentGradeDTO> findMyGrades() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof SecurityUser securityUser) {
            Student student = findByUserId(securityUser.getUser().getId());
            if (student != null) {
                return studentMapper.findGradesByStudentId(student.getId());
            }
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public boolean updateMyProfile(StudentProfileUpdateDTO updateDTO) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof SecurityUser securityUser)) {
            throw new BusinessException("无法获取当前用户信息");
        }
        User currentUser = userMapper.selectById(securityUser.getUser().getId());
        Student currentStudent = findByUserId(currentUser.getId());
        if (currentStudent == null) {
            throw new BusinessException("未找到当前用户的学生记录");
        }
        
        currentUser.setEmail(updateDTO.getEmail());
        currentUser.setPhone(updateDTO.getPhone());
        userMapper.updateById(currentUser);
        
        currentStudent.setStudentName(updateDTO.getStudentName());
        currentStudent.setGender(updateDTO.getGender());
        updateById(currentStudent);
        
        return true;
    }

    @Override
    public List<StudentCourseDTO> findMyCourses() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof SecurityUser securityUser) {
            Student student = findByUserId(securityUser.getUser().getId());
            if (student != null) {
                return studentMapper.findCoursesByStudentId(student.getId());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<StudentDTO> findUnassignedStudents() {
        // 1. 查询所有 class_id 为 null 的学生
        List<Student> students = this.list(
                new LambdaQueryWrapper<Student>().isNull(Student::getClassId)
                        .orderByDesc(Student::getCreatedTime)
        );

        if (students.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 转换为 DTO
        return students.stream().map(student -> {
            StudentDTO dto = new StudentDTO();
            BeanUtils.copyProperties(student, dto);
            // 因为未分配班级，所以班级相关的 DTO 字段（className）将为 null，这是符合预期的
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public IPage<StudentDTO> selectPageWithDetails(IPage<Student> page, String studentNumber, String studentName, Long classId) {
        return baseMapper.selectPageWithDetails(page, studentNumber, studentName, classId);
    }

    @Transactional
    @Override
    public int assignStudentsToClass(Long classId, List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return 0;
        }

        // 1. 批量查询所有待分配的学生
        List<Student> studentsToUpdate = this.listByIds(studentIds);

        // 2. 校验学生ID的有效性
        if (studentsToUpdate.size() != studentIds.size()) {
            List<Long> foundIds = studentsToUpdate.stream().map(Student::getId).collect(Collectors.toList());
            List<Long> notFoundIds = studentIds.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());
            throw new BusinessException("操作失败，以下学生ID不存在: " + notFoundIds);
        }

        // 3. 检查学生是否已经分配了班级
        List<String> alreadyAssignedStudents = studentsToUpdate.stream()
                .filter(student -> student.getClassId() != null)
                .map(student -> student.getStudentName() + "(" + student.getStudentNumber() + ")")
                .collect(Collectors.toList());

        if (!alreadyAssignedStudents.isEmpty()) {
            throw new BusinessException("操作失败，以下学生已分配班级: " + String.join(", ", alreadyAssignedStudents));
        }

        // 4. 更新学生的班级ID
        for (Student student : studentsToUpdate) {
            student.setClassId(classId);
        }

        // 5. 批量更新
        this.updateBatchById(studentsToUpdate);

        return studentsToUpdate.size();
    }

    @Override
    public List<StudentDTO> findStudentsByClassId(Long classId) {
        // 1. 根据班级ID查询学生列表
        List<Student> students = this.list(
                new LambdaQueryWrapper<Student>().eq(Student::getClassId, classId)
        );

        if (students.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 转换为 DTO
        return students.stream().map(student -> {
            StudentDTO dto = new StudentDTO();
            BeanUtils.copyProperties(student, dto);
            // 班级名称在此场景下是已知的，但为保持DTO结构一致性，此处不单独设置
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public int removeStudentsFromClass(Long classId, List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return 0;
        }

        // 1. 批量查询所有待移除的学生
        List<Student> studentsToUpdate = this.listByIds(studentIds);

        // 2. 校验学生ID的有效性
        if (studentsToUpdate.size() != studentIds.size()) {
            List<Long> foundIds = studentsToUpdate.stream().map(Student::getId).collect(Collectors.toList());
            List<Long> notFoundIds = studentIds.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());
            throw new BusinessException("操作失败，以下学生ID不存在: " + notFoundIds);
        }

        // 3. 检查这些学生是否都属于目标班级
        List<String> wrongClassStudents = studentsToUpdate.stream()
                .filter(student -> !classId.equals(student.getClassId()))
                .map(student -> {
                    String studentDesc = student.getStudentName() + "(" + student.getStudentNumber() + ")";
                    if (student.getClassId() == null) {
                        return studentDesc + " 未分配班级";
                    } else {
                        return studentDesc + " 属于其他班级";
                    }
                })
                .collect(Collectors.toList());

        if (!wrongClassStudents.isEmpty()) {
            throw new BusinessException("操作失败，以下学生不属于该班级: " + String.join(", ", wrongClassStudents));
        }

        // 4. 将学生的班级ID设置为null
        for (Student student : studentsToUpdate) {
            student.setClassId(null);
        }

        // 5. 批量更新
        this.updateBatchById(studentsToUpdate);

        return studentsToUpdate.size();
    }
}
