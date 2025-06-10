package com.ljp.xjt.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.dto.UserDTO;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 用户管理控制器
 * <p>
 * 提供用户管理相关的API接口，包括用户查询、创建、更新、禁用等功能
 * 主要供管理员使用，用于管理系统中的所有用户
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户管理", description = "用户管理相关接口")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     *
     * @param current 当前页码
     * @param size 每页大小
     * @param username 用户名（模糊查询）
     * @param email 邮箱（模糊查询）
     * @param status 状态
     * @return 包含角色信息的用户分页数据
     */
    @GetMapping
    @Operation(summary = "分页查询用户列表", description = "管理员分页查询系统中的所有用户，结果包含角色信息。")
    public ApiResponse<IPage<UserDTO>> getUserList(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") @Positive Long current,
            
            @Parameter(description = "每页大小", example = "10") 
            @RequestParam(defaultValue = "10") @Positive Long size,
            
            @Parameter(description = "用户名（模糊查询）")
            @RequestParam(required = false) String username,
            
            @Parameter(description = "邮箱（模糊查询）")
            @RequestParam(required = false) String email,
            
            @Parameter(description = "状态：0-禁用，1-正常")
            @RequestParam(required = false) Integer status) {
        
        log.info("Query user list - current: {}, size: {}, username: {}, email: {}, status: {}", 
                 current, size, username, email, status);
        
        Page<User> page = new Page<>(current, size);
        IPage<UserDTO> userDtoPage = userService.getUserList(page, username, email, status);
        
        return ApiResponse.success("查询成功", userDtoPage);
    }

    /**
     * 根据ID查询用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询用户详情", description = "根据用户ID查询用户详细信息")
    public ApiResponse<User> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long id) {
        
        log.info("Query user by ID: {}", id);
        
        // 1. 查询用户信息
        User user = userService.getById(id);
        if (user == null) {
            return ApiResponse.notFound();
        }
        
        // 2. 脱敏处理
        user.setPassword(null);
        
        return ApiResponse.success("查询成功", user);
    }

    /**
     * 创建新用户
     *
     * @param user 用户信息
     * @return 创建结果
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "管理员创建新用户")
    public ApiResponse<User> createUser(@Valid @RequestBody User user) {
        log.info("Create new user: {}", user.getUsername());
        
        // 1. 创建用户
        boolean result = userService.createUser(user);
        if (!result) {
            return ApiResponse.error("用户创建失败");
        }
        
        // 2. 脱敏处理
        user.setPassword(null);
        
        return ApiResponse.created(user);
    }

    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "管理员更新用户信息")
    public ApiResponse<User> updateUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody User user) {
        
        log.info("Update user: {}", id);
        
        // 1. 设置用户ID
        user.setId(id);
        
        // 2. 更新用户信息
        boolean result = userService.updateUser(user);
        if (!result) {
            return ApiResponse.error("用户更新失败");
        }
        
        // 3. 查询更新后的用户信息
        User updatedUser = userService.getById(id);
        if (updatedUser != null) {
            updatedUser.setPassword(null);
        }
        
        return ApiResponse.success("更新成功", updatedUser);
    }

    /**
     * 禁用用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用用户", description = "管理员禁用指定用户")
    public ApiResponse<Void> disableUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long id) {
        
        log.info("Disable user: {}", id);
        
        boolean result = userService.disableUser(id);
        if (!result) {
            return ApiResponse.error("用户禁用失败");
        }
        
        return ApiResponse.success();
    }

    /**
     * 启用用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PutMapping("/{id}/enable")
    @Operation(summary = "启用用户", description = "管理员启用指定用户")
    public ApiResponse<Void> enableUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long id) {
        
        log.info("Enable user: {}", id);
        
        boolean result = userService.enableUser(id);
        if (!result) {
            return ApiResponse.error("用户启用失败");
        }
        
        return ApiResponse.success();
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "管理员删除指定用户（软删除）")
    public ApiResponse<Void> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long id) {
        
        log.info("Delete user: {}", id);
        
        boolean result = userService.removeById(id);
        if (!result) {
            return ApiResponse.error("用户删除失败");
        }
        
        return ApiResponse.success();
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 检查结果
     */
    @GetMapping("/check-username")
    @Operation(summary = "检查用户名", description = "检查用户名是否已存在")
    public ApiResponse<Boolean> checkUsername(
            @Parameter(description = "用户名", required = true)
            @RequestParam String username) {
        
        boolean exists = userService.isUsernameExists(username);
        return ApiResponse.success("检查完成", exists);
    }

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 检查结果
     */
    @GetMapping("/check-email")
    @Operation(summary = "检查邮箱", description = "检查邮箱是否已存在")
    public ApiResponse<Boolean> checkEmail(
            @Parameter(description = "邮箱", required = true)
            @RequestParam String email) {
        
        boolean exists = userService.isEmailExists(email);
        return ApiResponse.success("检查完成", exists);
    }

} 