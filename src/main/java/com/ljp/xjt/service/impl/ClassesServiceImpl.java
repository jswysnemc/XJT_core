package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.entity.Classes;
import com.ljp.xjt.mapper.ClassesMapper;
import com.ljp.xjt.service.ClassesService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 班级服务实现类
 * <p>
 * 实现了 `ClassesService` 接口中定义的班级管理业务逻辑。
 * 使用 `ClassesMapper` 与数据库进行交互。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@Service
public class ClassesServiceImpl extends ServiceImpl<ClassesMapper, Classes> implements ClassesService {

    /**
     * 检查班级名称是否已存在。
     * 如果提供了 classId，则在检查时排除该 ID 对应的班级（用于更新操作）。
     *
     * @param className 班级名称
     * @param classId   当前班级ID (可为null，用于创建时)
     * @return boolean 如果名称已存在（排除自身后），则返回true，否则返回false
     */
    @Override
    public boolean checkClassNameExists(String className, Long classId) {
        if (!StringUtils.hasText(className)) {
            return false; // 名称为空不进行检查，或根据业务抛出异常
        }
        LambdaQueryWrapper<Classes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Classes::getClassName, className);
        if (classId != null) {
            queryWrapper.ne(Classes::getId, classId);
        }
        return baseMapper.exists(queryWrapper);
    }

    /**
     * 检查班级编码是否已存在。
     * 如果提供了 classId，则在检查时排除该 ID 对应的班级（用于更新操作）。
     *
     * @param classCode 班级编码
     * @param classId   当前班级ID (可为null，用于创建时)
     * @return boolean 如果编码已存在（排除自身后），则返回true，否则返回false
     */
    @Override
    public boolean checkClassCodeExists(String classCode, Long classId) {
        if (!StringUtils.hasText(classCode)) {
            return false; // 编码为空不进行检查，或根据业务抛出异常
        }
        LambdaQueryWrapper<Classes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Classes::getClassCode, classCode);
        if (classId != null) {
            queryWrapper.ne(Classes::getId, classId);
        }
        return baseMapper.exists(queryWrapper);
    }
} 