package com.ljp.xjt.controller;

import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.entity.Department;
import com.ljp.xjt.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 部门控制器
 * <p>
 *     负责处理部门相关的API请求
 * </p>
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@RestController
@RequestMapping("/admin/departments")
@Tag(name = "部门管理", description = "提供部门信息的增删改查接口")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * 获取所有部门列表
     *
     * @return ApiResponse<List<Department>> 部门列表
     */
    @GetMapping
    @Operation(summary = "获取所有部门列表", description = "查询并返回系统中所有的部门信息。")
    public ApiResponse<List<Department>> getAllDepartments() {
        // 1. 调用服务层获取所有部门
        List<Department> departmentList = departmentService.list();

        // 2. 返回成功响应
        return ApiResponse.success(departmentList);
    }
} 