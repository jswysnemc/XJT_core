package com.ljp.xjt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljp.xjt.entity.Classes;

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

    // 未来可以添加更多业务方法，例如：
    // Page<ClassesDTO> findClassesWithDetails(Page<Classes> page, ClassesQueryParam queryParam);
    // void assignStudentsToClass(Long classId, List<Long> studentIds);
} 