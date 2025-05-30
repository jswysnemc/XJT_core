package com.ljp.xjt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ljp.xjt.entity.Teacher;

import java.util.List;

/**
 * 教师服务接口
 * <p>
 * 提供教师管理相关业务逻辑处理
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
public interface TeacherService extends IService<Teacher> {

    /**
     * 分页查询教师列表
     *
     * @param page 分页参数
     * @param teacherName 教师姓名(模糊查询)
     * @param departmentId 部门ID
     * @return 教师分页列表
     */
    IPage<Teacher> getTeacherList(Page<Teacher> page, String teacherName, Long departmentId);

    /**
     * 创建教师
     *
     * @param teacher 教师信息
     * @return 是否成功
     */
    boolean createTeacher(Teacher teacher);

    /**
     * 更新教师
     *
     * @param teacher 教师信息
     * @return 是否成功
     */
    boolean updateTeacher(Teacher teacher);

    /**
     * 查找教师信息
     *
     * @param id 教师ID
     * @return 教师信息
     */
    Teacher getTeacherById(Long id);

    /**
     * 根据用户ID查询教师信息
     *
     * @param userId 用户ID
     * @return 教师信息
     */
    Teacher getTeacherByUserId(Long userId);

    /**
     * 检查教工号是否已存在
     *
     * @param teacherNumber 教工号
     * @return 是否存在
     */
    boolean checkTeacherNumberExists(String teacherNumber);

    /**
     * 获取所有教师(用于下拉选择)
     *
     * @return 教师列表
     */
    List<Teacher> getAllTeachers();

} 