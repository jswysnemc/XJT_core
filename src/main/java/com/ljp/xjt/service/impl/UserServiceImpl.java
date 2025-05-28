package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.common.exception.BusinessException;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.mapper.RoleMapper;
import com.ljp.xjt.mapper.UserMapper;
import com.ljp.xjt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * 用户服务实现类
 * <p>
 * 实现用户相关的业务逻辑，包括用户管理、认证等功能
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final @Lazy PasswordEncoder passwordEncoder;

    @Override
    public User getUserByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return baseMapper.selectByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        return baseMapper.selectByEmail(email);
    }

    @Override
    public User getUserByPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return null;
        }
        return baseMapper.selectByPhone(phone);
    }

    @Override
    @Transactional
    public boolean createUser(User user) {
        if (isUsernameExists(user.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        if (StringUtils.hasText(user.getEmail()) && isEmailExists(user.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }
        if (StringUtils.hasText(user.getPhone()) && isPhoneExists(user.getPhone())) {
            throw new BusinessException("手机号已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return save(user);
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        User existingUser = getById(user.getId());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        // 检查邮箱和手机号是否与其他用户冲突
        if (StringUtils.hasText(user.getEmail()) && !user.getEmail().equals(existingUser.getEmail()) && isEmailExists(user.getEmail())) {
            throw new BusinessException("邮箱已被其他用户使用");
        }
        if (StringUtils.hasText(user.getPhone()) && !user.getPhone().equals(existingUser.getPhone()) && isPhoneExists(user.getPhone())) {
            throw new BusinessException("手机号已被其他用户使用");
        }

        // 如果密码字段不为空，则更新密码
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 否则，保持原有密码不变
            user.setPassword(existingUser.getPassword());
        }
        return updateById(user);
    }

    @Override
    @Transactional
    public boolean disableUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(0); // 0 表示禁用
        return updateById(user);
    }

    @Override
    @Transactional
    public boolean enableUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(1); // 1 表示启用
        return updateById(user);
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<User> getUserList(Page<User> page, String username, String email, Integer status) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(email)) {
            queryWrapper.like(User::getEmail, email);
        }
        if (status != null) {
            queryWrapper.eq(User::getStatus, status);
        }
        queryWrapper.orderByDesc(User::getCreatedTime);
        return userMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return count(new LambdaQueryWrapper<User>().eq(User::getUsername, username)) > 0;
    }

    @Override
    public boolean isEmailExists(String email) {
        if (!StringUtils.hasText(email)) return false;
        return count(new LambdaQueryWrapper<User>().eq(User::getEmail, email)) > 0;
    }

    @Override
    public boolean isPhoneExists(String phone) {
        if (!StringUtils.hasText(phone)) return false;
        return count(new LambdaQueryWrapper<User>().eq(User::getPhone, phone)) > 0;
    }

    /**
     * 根据用户名查询用户（包含角色信息）
     *
     * @param username 用户名
     * @return 用户信息（包含角色），如果不存在则返回null
     */
    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user != null) {
            // 查询并设置用户的角色信息
            Set<com.ljp.xjt.entity.Role> roles = roleMapper.findRolesByUserId(user.getId());
            user.setRoles(roles);
        }
        return user;
    }

} 