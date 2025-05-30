package com.ljp.xjt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljp.xjt.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关联Mapper接口
 * <p>
 * 提供用户角色关联的数据访问操作
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响的行数
     */
    int assignRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 移除用户的角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响的行数
     */
    int removeRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 移除用户的所有角色
     *
     * @param userId 用户ID
     * @return 影响的行数
     */
    int removeAllRoles(@Param("userId") Long userId);
} 