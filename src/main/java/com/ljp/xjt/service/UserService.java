package com.ljp.xjt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ljp.xjt.dto.UnboundUserDTO;
import com.ljp.xjt.dto.UserDTO;
import com.ljp.xjt.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * 用户服务接口
 * <p>
 * 提供用户相关的业务操作，包括用户管理、认证等功能
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByEmail(String email);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User getUserByPhone(String phone);

    /**
     * 创建新用户
     *
     * @param user 用户信息
     * @return 创建结果
     */
    boolean createUser(User user);

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新结果
     */
    boolean updateUser(User user);

    /**
     * 禁用用户
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean disableUser(Long userId);

    /**
     * 启用用户
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean enableUser(Long userId);

    /**
     * 分页查询用户列表
     *
     * @param page      分页对象
     * @param username  用户名
     * @param email     邮箱
     * @param status    状态
     * @return 包含角色信息的用户分页数据
     */
    IPage<UserDTO> getUserList(Page<User> page, String username, String email, Integer status);

    /**
     * 验证用户名是否已存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean isUsernameExists(String username);

    /**
     * 验证邮箱是否已存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean isEmailExists(String email);

    /**
     * 验证手机号是否已存在
     *
     * @param phone 手机号
     * @return 是否存在
     */
    boolean isPhoneExists(String phone);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息，如果不存在则返回null
     */
    User findByUsername(String username);

    /**
     * 根据用户ID查询用户
     * @param id
     * @return
     */
    User findById(Long id);

    /**
     * 修改用户密码
     * @param user
     * @param oldPassword
     * @param newPassword
     */
    void changePassword(User user, String oldPassword, String newPassword);

    /**
     * 查询未绑定学生的用户
     *
     * @return List<UnboundUserDTO>
     */
    List<UnboundUserDTO> findUnboundStudentUsers();

} 