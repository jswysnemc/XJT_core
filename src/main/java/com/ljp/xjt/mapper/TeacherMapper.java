package com.ljp.xjt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
} 