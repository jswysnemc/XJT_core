package com.ljp.xjt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ljp.xjt.dto.*;
import com.ljp.xjt.entity.Student;

import java.util.List;

/**
 * 学生服务接口
 * <p>
 * 定义学生相关的业务逻辑
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
public interface StudentService extends IService<Student> {

    /**
     * 分页查询学生列表
     *
     * @param page 分页对象
     * @param studentName 学生姓名（模糊查询）
     * @return 学生分页数据
     */
    IPage<Student> list(Page<Student> page, String studentName);

    /**
     * 分页查询学生列表，并携带班级等详细信息
     * @param page 分页对象
     * @param queryWrapper 查询条件
     * @return 包含详细信息的学生分页数据
     */
    IPage<StudentDTO> selectStudentPage(Page<Student> page, LambdaQueryWrapper<Student> queryWrapper);

    /**
     * 新增学生
     *
     * @param student 学生信息
     * @return 是否成功
     */
    boolean saveStudent(Student student);

    /**
     * 更新学生
     *
     * @param student 学生信息
     * @return 是否成功
     */
    boolean updateStudent(Student student);

    /**
     * 根据用户ID获取学生档案
     *
     * @param userId 用户ID
     * @return 学生档案DTO
     */
    StudentProfileDTO getStudentProfileByUserId(Long userId);

    /**
     * 更新学生个人档案
     *
     * @param userId 用户ID
     * @param studentProfileUpdateDTO 更新的档案信息
     */
    void updateStudentProfile(Long userId, StudentProfileUpdateDTO studentProfileUpdateDTO);

    /**
     * 获取学生的课程列表
     *
     * @param userId 用户ID
     * @return 课程列表
     */
    List<StudentCourseDTO> getStudentCourses(Long userId);

    /**
     * 获取学生的成绩单
     *
     * @param userId 用户ID
     * @return 成绩列表
     */
    List<StudentGradeDTO> getStudentGrades(Long userId);

    /**
     * 根据学生ID获取学生详细信息（包含班级名称）
     *
     * @param studentId 学生ID
     * @return 学生详细信息DTO
     */
    StudentDetailDTO getStudentDetailById(Long studentId);

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

    /**
     * 更新当前登录学生的个人信息
     *
     * @param updateDTO 包含待更新信息的DTO
     * @return boolean 更新是否成功
     */
    boolean updateMyProfile(StudentProfileUpdateDTO updateDTO);

    /**
     * 查询当前登录学生的所有课程信息
     *
     * @return List<StudentCourseDTO> 包含课程详情的列表
     */
    List<StudentCourseDTO> findMyCourses();

    IPage<StudentDTO> selectPageWithDetails(IPage<Student> page, String studentNumber, String studentName, Long classId);

    List<StudentDTO> findUnassignedStudents();

    int assignStudentsToClass(Long classId, List<Long> studentIds);

    List<StudentDTO> findStudentsByClassId(Long classId);

    int removeStudentsFromClass(Long classId, List<Long> studentIds);

    // 未来可以添加更多业务方法，例如：
    // Page<StudentDTO> findStudentsWithDetails(Page<Student> page, StudentQueryParam queryParam);
} 