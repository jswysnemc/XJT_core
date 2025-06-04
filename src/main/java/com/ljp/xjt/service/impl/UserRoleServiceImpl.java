package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.entity.Role;
import com.ljp.xjt.entity.UserRole;
import com.ljp.xjt.mapper.UserRoleMapper;
import com.ljp.xjt.service.RoleService;
import com.ljp.xjt.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户角色关联服务实现类
 * <p>
 * 实现用户角色关联的业务逻辑，如角色分配、移除等
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    private final UserRoleMapper userRoleMapper;
    private final RoleService roleService;

    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean assignRole(Long userId, Long roleId) {
        // 1. 检查是否已经分配了该角色
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId)
                   .eq(UserRole::getRoleId, roleId);
        
        long count = count(queryWrapper);
        if (count > 0) {
            log.info("User {} already has role {}", userId, roleId);
            return true;
        }
        
        // 2. 分配角色
        int result = userRoleMapper.assignRole(userId, roleId);
        
        log.info("Assigned role {} to user {}, result: {}", roleId, userId, result > 0);
        return result > 0;
    }

    /**
     * 为用户分配角色（根据角色编码）
     *
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean assignRoleByCode(Long userId, String roleCode) {
        if (userId == null || !StringUtils.hasText(roleCode)) {
            return false;
        }
        
        // 1. 根据角色编码查询角色ID
        LambdaQueryWrapper<Role> roleQuery = new LambdaQueryWrapper<>();
        roleQuery.eq(Role::getRoleCode, roleCode);
        
        Role role = roleService.getOne(roleQuery);
        if (role == null) {
            log.warn("Role with code {} not found", roleCode);
            return false;
        }
        
        // 2. 分配角色
        return assignRole(userId, role.getId());
    }

    /**
     * 移除用户的角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean removeRole(Long userId, Long roleId) {
        int result = userRoleMapper.removeRole(userId, roleId);
        
        log.info("Removed role {} from user {}, result: {}", roleId, userId, result > 0);
        return result > 0;
    }

    /**
     * 通过角色编码移除用户角色
     *
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean removeRoleByCode(Long userId, String roleCode) {
        if (userId == null || !StringUtils.hasText(roleCode)) {
            return false;
        }
        
        // 1. 根据角色编码查询角色ID
        LambdaQueryWrapper<Role> roleQuery = new LambdaQueryWrapper<>();
        roleQuery.eq(Role::getRoleCode, roleCode);
        
        Role role = roleService.getOne(roleQuery);
        if (role == null) {
            log.warn("Role with code {} not found", roleCode);
            return false;
        }
        
        // 2. 移除角色
        return removeRole(userId, role.getId());
    }

    /**
     * 移除用户的所有角色
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean removeAllRoles(Long userId) {
        int result = userRoleMapper.removeAllRoles(userId);
        
        log.info("Removed all roles from user {}, affected rows: {}", userId, result);
        return result > 0;
    }
    
    /**
     * 检查用户是否拥有指定角色
     *
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否拥有该角色
     */
    @Override
    public boolean hasRole(Long userId, String roleCode) {
        if (userId == null || !StringUtils.hasText(roleCode)) {
            return false;
        }
        
        // 1. 根据角色编码查询角色ID
        LambdaQueryWrapper<Role> roleQuery = new LambdaQueryWrapper<>();
        roleQuery.eq(Role::getRoleCode, roleCode);
        
        Role role = roleService.getOne(roleQuery);
        if (role == null) {
            log.warn("Role with code {} not found", roleCode);
            return false;
        }
        
        // 2. 查询用户是否有该角色
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId)
                   .eq(UserRole::getRoleId, role.getId());
        
        return count(queryWrapper) > 0;
    }
} 