package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.common.exception.BusinessException;
import com.ljp.xjt.entity.Role;
import com.ljp.xjt.entity.UserRole;
import com.ljp.xjt.mapper.RoleMapper;
import com.ljp.xjt.mapper.UserRoleMapper;
import com.ljp.xjt.service.RoleService;
import com.ljp.xjt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 角色服务实现类
 * <p>
 * 实现角色相关的业务逻辑。
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper; // 显式注入RoleMapper，虽然ServiceImpl已包含，但便于直接调用自定义方法
    private final UserRoleMapper userRoleMapper; // 注入UserRoleMapper用于检查角色关联
    private final UserService userService; // 注入UserService用于验证用户是否存在

    @Override
    public Set<Role> findRolesByUserId(Long userId) {
        if (userId == null) {
            return Set.of(); // 或者抛出异常，根据业务决定
        }
        return roleMapper.findRolesByUserId(userId);
    }

    @Override
    public IPage<Role> getRoleList(Page<Role> page, String roleName, String roleCode) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(roleName)) {
            queryWrapper.like(Role::getRoleName, roleName);
        }
        if (StringUtils.hasText(roleCode)) {
            queryWrapper.like(Role::getRoleCode, roleCode);
        }
        queryWrapper.orderByDesc(Role::getCreatedTime);
        return page(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean createRole(Role role) {
        if (role == null || !StringUtils.hasText(role.getRoleName()) || !StringUtils.hasText(role.getRoleCode())) {
            throw new BusinessException("角色名称和角色编码不能为空");
        }
        if (isRoleNameExists(role.getRoleName())) {
            throw new BusinessException("角色名称已存在: " + role.getRoleName());
        }
        if (isRoleCodeExists(role.getRoleCode())) {
            throw new BusinessException("角色编码已存在: " + role.getRoleCode());
        }
        log.info("Creating new role: name={}, code={}", role.getRoleName(), role.getRoleCode());
        return save(role);
    }

    @Override
    @Transactional
    public boolean updateRole(Role role) {
        if (role == null || role.getId() == null) {
            throw new BusinessException("角色ID不能为空");
        }
        Role existingRole = getById(role.getId());
        if (existingRole == null) {
            throw new BusinessException("角色不存在: ID=" + role.getId());
        }

        if (StringUtils.hasText(role.getRoleName()) && !role.getRoleName().equals(existingRole.getRoleName())) {
            if (isRoleNameExists(role.getRoleName())) {
                throw new BusinessException("角色名称已存在: " + role.getRoleName());
            }
        }
        if (StringUtils.hasText(role.getRoleCode()) && !role.getRoleCode().equals(existingRole.getRoleCode())) {
            if (isRoleCodeExists(role.getRoleCode())) {
                throw new BusinessException("角色编码已存在: " + role.getRoleCode());
            }
        }
        log.info("Updating role: id={}, name={}, code={}", role.getId(), role.getRoleName(), role.getRoleCode());
        return updateById(role);
    }

    @Override
    @Transactional
    public boolean deleteRole(Long roleId) {
        if (roleId == null) {
            throw new BusinessException("角色ID不能为空");
        }
        
        // 实际项目中，删除角色前应检查该角色是否被用户关联，如果有关联，根据策略处理（例如禁止删除，或先解除关联）
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getRoleId, roleId);
        long userCount = userRoleMapper.selectCount(queryWrapper);
        
        if (userCount > 0) {
            log.warn("Cannot delete role with ID {} because it is associated with {} users", roleId, userCount);
            throw new BusinessException("角色正在被" + userCount + "个用户使用，无法删除。请先解除这些用户的角色关联");
        }
        
        log.info("Deleting role with ID: {}", roleId);
        return removeById(roleId);
    }

    @Override
    public boolean isRoleNameExists(String roleName) {
        if (!StringUtils.hasText(roleName)) {
            return false;
        }
        return count(new LambdaQueryWrapper<Role>().eq(Role::getRoleName, roleName)) > 0;
    }

    @Override
    public boolean isRoleCodeExists(String roleCode) {
        if (!StringUtils.hasText(roleCode)) {
            return false;
        }
        return count(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, roleCode)) > 0;
    }
    
    @Override
    @Transactional
    public boolean assignRoleToUser(Long userId, Long roleId) {
        // 验证参数
        if (userId == null || roleId == null) {
            log.warn("Cannot assign role: userId={}, roleId={}", userId, roleId);
            throw new BusinessException("用户ID和角色ID不能为空");
        }
        
        // 验证用户和角色是否存在
        if (userService.getById(userId) == null) {
            throw new BusinessException("用户不存在: ID=" + userId);
        }
        
        if (getById(roleId) == null) {
            throw new BusinessException("角色不存在: ID=" + roleId);
        }
        
        // 查询用户是否已拥有该角色
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId)
                   .eq(UserRole::getRoleId, roleId);
        
        if (userRoleMapper.selectCount(queryWrapper) > 0) {
            // 用户已有该角色，直接返回成功
            log.info("User {} already has role {}", userId, roleId);
            return true;
        }
        
        // 添加用户角色关联
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        
        int result = userRoleMapper.insert(userRole);
        log.info("Assigned role {} to user {}, result: {}", roleId, userId, result > 0);
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean assignRolesToUser(Long userId, List<Long> roleIds) {
        // 验证参数
        if (userId == null || roleIds == null || roleIds.isEmpty()) {
            log.warn("Cannot assign roles: userId={}, roleIds={}", userId, roleIds);
            throw new BusinessException("用户ID和角色ID列表不能为空");
        }
        
        // 验证用户是否存在
        if (userService.getById(userId) == null) {
            throw new BusinessException("用户不存在: ID=" + userId);
        }
        
        // 验证所有角色是否存在
        for (Long roleId : roleIds) {
            if (getById(roleId) == null) {
                throw new BusinessException("角色不存在: ID=" + roleId);
            }
        }
        
        // 获取用户已有的角色
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId)
                   .in(UserRole::getRoleId, roleIds);
        
        List<UserRole> existingRoles = userRoleMapper.selectList(queryWrapper);
        Set<Long> existingRoleIds = existingRoles.stream()
            .map(UserRole::getRoleId)
            .collect(java.util.stream.Collectors.toSet());
        
        // 批量添加用户角色关联
        List<UserRole> toInsert = new ArrayList<>();
        for (Long roleId : roleIds) {
            if (!existingRoleIds.contains(roleId)) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                toInsert.add(userRole);
            }
        }
        
        if (toInsert.isEmpty()) {
            log.info("User {} already has all specified roles", userId);
            return true;
        }
        
        int result = 0;
        for (UserRole userRole : toInsert) {
            result += userRoleMapper.insert(userRole);
        }
        
        log.info("Assigned {} roles to user {}, inserted: {}", toInsert.size(), userId, result);
        return result == toInsert.size();
    }
    
    @Override
    @Transactional
    public boolean removeRoleFromUser(Long userId, Long roleId) {
        // 验证参数
        if (userId == null || roleId == null) {
            log.warn("Cannot remove role: userId={}, roleId={}", userId, roleId);
            throw new BusinessException("用户ID和角色ID不能为空");
        }
        
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId)
                   .eq(UserRole::getRoleId, roleId);
        
        int result = userRoleMapper.delete(queryWrapper);
        log.info("Removed role {} from user {}, result: {}", roleId, userId, result > 0);
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean removeRolesFromUser(Long userId, List<Long> roleIds) {
        // 验证参数
        if (userId == null || roleIds == null || roleIds.isEmpty()) {
            log.warn("Cannot remove roles: userId={}, roleIds={}", userId, roleIds);
            throw new BusinessException("用户ID和角色ID列表不能为空");
        }
        
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId)
                   .in(UserRole::getRoleId, roleIds);
        
        int result = userRoleMapper.delete(queryWrapper);
        log.info("Removed {} roles from user {}", result, userId);
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean removeAllRolesFromUser(Long userId) {
        // 验证参数
        if (userId == null) {
            log.warn("Cannot remove all roles: userId is null");
            throw new BusinessException("用户ID不能为空");
        }
        
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId);
        
        int result = userRoleMapper.delete(queryWrapper);
        log.info("Removed all roles from user {}, count: {}", userId, result);
        return result >= 0; // 即使用户没有角色，也视为成功
    }
    
    @Override
    public Role getRoleByCode(String roleCode) {
        if (!StringUtils.hasText(roleCode)) {
            return null;
        }
        
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleCode, roleCode);
        
        return getOne(queryWrapper);
    }
} 