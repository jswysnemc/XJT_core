package com.ljp.xjt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljp.xjt.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * 角色Mapper接口
 * <p>
 * 提供角色相关的数据访问操作，包括角色的CRUD以及用户角色的关联查询。
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据用户ID查询该用户拥有的所有角色
     *
     * @param userId 用户ID
     * @return 用户拥有的角色集合
     */
    Set<Role> findRolesByUserId(@Param("userId") Long userId);

} 