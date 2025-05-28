package com.ljp.xjt.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.entity.Role;
import com.ljp.xjt.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理控制器
 * <p>
 * 提供角色管理相关的API接口，如角色创建、查询、更新、删除。
 * 所有接口均需要管理员权限。
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@Slf4j
@RestController
@RequestMapping("/admin/roles") // 路径前缀 /api 已由 context-path 处理，这里是 /admin/roles
@RequiredArgsConstructor
@Validated
@Tag(name = "角色管理", description = "角色管理相关接口 (需要管理员权限)")
@PreAuthorize("hasRole('ADMIN')") // 类级别权限控制，要求ADMIN角色
public class RoleController {

    private final RoleService roleService;

    /**
     * 分页查询角色列表
     *
     * @param current 当前页码
     * @param size 每页大小
     * @param roleName 角色名称（可选，模糊查询）
     * @param roleCode 角色编码（可选，模糊查询）
     * @return 角色分页数据
     */
    @GetMapping
    @Operation(summary = "分页查询角色列表")
    public ApiResponse<IPage<Role>> getRoleList(
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") @Positive Long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") @Positive Long size,
            @Parameter(description = "角色名称") @RequestParam(required = false) String roleName,
            @Parameter(description = "角色编码") @RequestParam(required = false) String roleCode) {
        log.info("Fetching role list: current={}, size={}, roleName={}, roleCode={}", current, size, roleName, roleCode);
        Page<Role> page = new Page<>(current, size);
        IPage<Role> rolePage = roleService.getRoleList(page, roleName, roleCode);
        return ApiResponse.success("查询成功", rolePage);
    }

    /**
     * 根据ID查询角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询角色详情")
    public ApiResponse<Role> getRoleById(
            @Parameter(description = "角色ID", required = true) @PathVariable @NotNull @Positive Long id) {
        log.info("Fetching role by ID: {}", id);
        Role role = roleService.getById(id);
        if (role == null) {
            return ApiResponse.notFound();
        }
        return ApiResponse.success("查询成功", role);
    }

    /**
     * 创建新角色
     *
     * @param role 角色信息
     * @return 创建的角色信息
     */
    @PostMapping
    @Operation(summary = "创建新角色")
    public ApiResponse<Role> createRole(@Valid @RequestBody Role role) {
        log.info("Creating new role: {}", role.getRoleName());
        roleService.createRole(role);
        // 通常create成功后，role对象会包含ID (如果ID是数据库生成的，且MyBatis Plus配置正确)
        return ApiResponse.created(role);
    }

    /**
     * 更新角色信息
     *
     * @param id 角色ID
     * @param role 角色信息
     * @return 更新后的角色信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新角色信息")
    public ApiResponse<Role> updateRole(
            @Parameter(description = "角色ID", required = true) @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody Role role) {
        log.info("Updating role with ID: {}", id);
        role.setId(id); // 确保ID被设置
        roleService.updateRole(role);
        Role updatedRole = roleService.getById(id);
        return ApiResponse.success("更新成功", updatedRole);
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public ApiResponse<Void> deleteRole(
            @Parameter(description = "角色ID", required = true) @PathVariable @NotNull @Positive Long id) {
        log.info("Deleting role with ID: {}", id);
        boolean deleted = roleService.deleteRole(id);
        if (deleted) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error("删除角色失败");
        }
    }
} 