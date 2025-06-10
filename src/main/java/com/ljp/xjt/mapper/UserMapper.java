package com.ljp.xjt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljp.xjt.dto.UnboundUserDTO;
import com.ljp.xjt.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 用户Mapper接口
 * <p>
 * 继承MyBatis Plus的BaseMapper，提供基础的CRUD操作
 * 可以添加自定义的SQL操作方法
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户信息
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User selectByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User selectByPhone(@Param("phone") String phone);

    /**
     * 根据用户名查询用户，并附带其角色信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsernameWithRoles(@Param("username") String username);

    /**
     * 根据角色编码查询拥有该角色的所有用户ID
     *
     * @param roleCode 角色编码
     * @return 用户ID集合
     */
    Set<Long> findUserIdsByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 查询所有拥有学生角色但未绑定学生记录的用户
     *
     * @return List<UnboundUserDTO> 未绑定用户的列表
     */
    @Select("""
            SELECT u.id, u.username
            FROM users u
            INNER JOIN user_roles ur ON u.id = ur.user_id
            INNER JOIN roles r ON ur.role_id = r.id
            LEFT JOIN students s ON u.id = s.user_id
            WHERE r.role_code = 'STUDENT' AND s.id IS NULL
            """)
    List<UnboundUserDTO> findUnboundStudentUsers();

} 