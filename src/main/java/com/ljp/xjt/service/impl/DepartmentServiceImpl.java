package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.entity.Department;
import com.ljp.xjt.mapper.DepartmentMapper;
import com.ljp.xjt.service.DepartmentService;
import org.springframework.stereotype.Service;

/**
 * 部门服务实现类
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
} 