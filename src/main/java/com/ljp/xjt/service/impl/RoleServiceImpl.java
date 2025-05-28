package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.common.exception.BusinessException;
import com.ljp.xjt.entity.Role;
import com.ljp.xjt.mapper.RoleMapper;
import com.ljp.xjt.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
        // TODO: 实际项目中，删除角色前应检查该角色是否被用户关联，如果有关联，根据策略处理（例如禁止删除，或先解除关联）
        // long userCount = userRoleMapper.selectCount(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, roleId));
        // if (userCount > 0) {
        //     throw new BusinessException("角色正在被用户使用，无法删除");
        // }
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
} 