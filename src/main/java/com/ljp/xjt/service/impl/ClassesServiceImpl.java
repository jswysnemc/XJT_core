package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.dto.ClassDto;
import com.ljp.xjt.entity.Classes;
import com.ljp.xjt.entity.Major;
import com.ljp.xjt.mapper.ClassesMapper;
import com.ljp.xjt.mapper.MajorMapper;
import com.ljp.xjt.service.ClassesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private MajorMapper majorMapper;

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

    @Override
    public Page<ClassDto> selectPageWithMajor(Page<Classes> page, LambdaQueryWrapper<Classes> wrapper) {
        // 1. 执行分页查询，获取基础的班级信息
        Page<Classes> classesPage = baseMapper.selectPage(page, wrapper);

        // 2. 如果查询结果为空，直接返回一个空的DTO分页对象
        if (classesPage.getRecords().isEmpty()) {
            return new Page<ClassDto>().setTotal(0);
        }

        // 3. 提取所有班级记录中的 majorId
        List<Long> majorIds = classesPage.getRecords().stream()
                .map(Classes::getMajorId)
                .distinct()
                .collect(Collectors.toList());

        // 4. 根据 majorId 批量查询专业信息
        Map<Long, String> majorIdToNameMap = majorMapper.selectBatchIds(majorIds).stream()
                .collect(Collectors.toMap(Major::getId, Major::getMajorName));

        // 5. 将 `Page<Classes>` 转换为 `Page<ClassDto>`，并填充 `majorName`
        Page<ClassDto> dtoPage = new Page<>(classesPage.getCurrent(), classesPage.getSize(), classesPage.getTotal());
        List<ClassDto> dtoList = classesPage.getRecords().stream().map(classes -> {
            ClassDto dto = new ClassDto();
            BeanUtils.copyProperties(classes, dto);
            dto.setMajorName(majorIdToNameMap.get(classes.getMajorId()));
            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public Map<Long, String> getMajorIdToNameMap(List<Long> majorIds) {
        if (majorIds == null || majorIds.isEmpty()) {
            return Map.of();
        }
        return majorMapper.selectBatchIds(majorIds).stream()
                .collect(Collectors.toMap(Major::getId, Major::getMajorName));
    }
} 