package com.ljp.xjt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.entity.Major;
import com.ljp.xjt.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 专业管理控制器
 * <p>
 * 提供专业信息的RESTful API接口。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-09
 */
@RestController
@RequestMapping("/admin/majors")
@Tag(name = "专业管理", description = "提供专业信息的查询接口")
@PreAuthorize("hasRole('ADMIN')")
public class MajorController {

    private final MajorService majorService;

    @Autowired
    public MajorController(MajorService majorService) {
        this.majorService = majorService;
    }

    /**
     * 获取所有专业列表 (不分页)
     *
     * @return ApiResponse<List<Major>> 所有专业列表
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有专业列表", description = "查询所有专业信息，不进行分页")
    public ApiResponse<List<Major>> listAllMajors() {
        List<Major> list = majorService.list(new LambdaQueryWrapper<Major>().orderByAsc(Major::getMajorName));
        return ApiResponse.success(list);
    }
} 