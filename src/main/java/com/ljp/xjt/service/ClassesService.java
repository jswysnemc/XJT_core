package com.ljp.xjt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljp.xjt.dto.ClassDto;
import com.ljp.xjt.entity.Classes;

import java.util.List;
import java.util.Map;

/**
 * 班级服务接口
 * <p>
 * 继承自MyBatis Plus的IService，提供基础的班级管理业务操作。
 * 定义了针对班级模块的业务逻辑方法。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
public interface ClassesService extends IService<Classes> {

    /**
     * 检查班级名称是否已存在
     *
     * @param className 班级名称
     * @param classId   当前班级ID (用于更新时排除自身)
     * @return boolean 如果存在则返回true，否则返回false
     */
    boolean checkClassNameExists(String className, Long classId);

    /**
     * 检查班级编码是否已存在
     *
     * @param classCode 班级编码
     * @param classId   当前班级ID (用于更新时排除自身)
     * @return boolean 如果存在则返回true，否则返回false
     */
    boolean checkClassCodeExists(String classCode, Long classId);

    /**
     * 分页查询班级列表（包含专业信息）
     *
     * @param page      分页对象
     * @param wrapper   查询条件
     * @return 分页的班级DTO列表
     */
    Page<ClassDto> selectPageWithMajor(Page<Classes> page, LambdaQueryWrapper<Classes> wrapper);

    /**
     * 根据专业ID列表获取 专业ID -> 专业名称 的映射
     * @param majorIds 专业ID列表
     * @return Map<Long, String>
     */
    Map<Long, String> getMajorIdToNameMap(List<Long> majorIds);

    // 未来可以添加更多业务方法，例如：
    // Page<ClassesDTO> findClassesWithDetails(Page<Classes> page, ClassesQueryParam queryParam);
    // void assignStudentsToClass(Long classId, List<Long> studentIds);
} 