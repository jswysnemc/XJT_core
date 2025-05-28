package com.ljp.xjt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ljp.xjt.entity.Role;

import java.util.Set;

/**
 * 角色服务接口
 * <p>
 * 提供角色相关的业务操作，如角色创建、查询、更新、删除以及用户角色管理等。
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
public interface RoleService extends IService<Role> {

    /**
     * 根据用户ID查询该用户拥有的所有角色
     *
     * @param userId 用户ID
     * @return 用户拥有的角色集合
     */
    Set<Role> findRolesByUserId(Long userId);

    /**
     * 分页查询角色列表
     *
     * @param page 分页参数
     * @param roleName 角色名称（模糊查询，可选）
     * @param roleCode 角色编码（模糊查询，可选）
     * @return 角色分页数据
     */
    IPage<Role> getRoleList(Page<Role> page, String roleName, String roleCode);

    /**
     * 创建新角色
     *
     * @param role 角色信息
     * @return 创建结果，成功返回true，失败返回false
     */
    boolean createRole(Role role);

    /**
     * 更新角色信息
     *
     * @param role 角色信息
     * @return 更新结果，成功返回true，失败返回false
     */
    boolean updateRole(Role role);

    /**
     * 删除角色
     * <p>注意：删除角色前应检查是否有用户关联此角色，或进行相应处理。</p>
     *
     * @param roleId 角色ID
     * @return 删除结果，成功返回true，失败返回false
     */
    boolean deleteRole(Long roleId);

    /**
     * 检查角色名称是否存在
     *
     * @param roleName 角色名称
     * @return 如果存在返回true，否则返回false
     */
    boolean isRoleNameExists(String roleName);

    /**
     * 检查角色编码是否存在
     *
     * @param roleCode 角色编码
     * @return 如果存在返回true，否则返回false
     */
    boolean isRoleCodeExists(String roleCode);
    
    // TODO: 后续可以添加为用户分配/移除角色的接口
} 