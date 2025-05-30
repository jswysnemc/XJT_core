package com.ljp.xjt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljp.xjt.entity.UserRole;

/**
 * 用户角色关联服务接口
 * <p>
 * 提供用户角色关联的业务操作，如角色分配、移除等
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
public interface UserRoleService extends IService<UserRole> {

    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean assignRole(Long userId, Long roleId);

    /**
     * 为用户分配角色（根据角色编码）
     *
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否成功
     */
    boolean assignRoleByCode(Long userId, String roleCode);

    /**
     * 移除用户的角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean removeRole(Long userId, Long roleId);

    /**
     * 通过角色编码移除用户角色
     *
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否成功
     */
    boolean removeRoleByCode(Long userId, String roleCode);

    /**
     * 移除用户的所有角色
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean removeAllRoles(Long userId);

    /**
     * 检查用户是否拥有指定角色
     *
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否拥有该角色
     */
    boolean hasRole(Long userId, String roleCode);
} 