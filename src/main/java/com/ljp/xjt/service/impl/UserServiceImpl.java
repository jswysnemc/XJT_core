package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljp.xjt.common.exception.BusinessException;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.mapper.UserMapper;
import com.ljp.xjt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
    public boolean createUser(User user) {
        // 1. 参数验证
        if (user == null || !StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            throw new BusinessException("用户名和密码不能为空");
        }

        // 2. 检查用户名是否已存在
        if (isUsernameExists(user.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 3. 检查邮箱是否已存在
        if (StringUtils.hasText(user.getEmail()) && isEmailExists(user.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }

        // 4. 检查手机号是否已存在
        if (StringUtils.hasText(user.getPhone()) && isPhoneExists(user.getPhone())) {
            throw new BusinessException("手机号已存在");
        }

        // 5. 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 6. 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(1); // 默认启用
        }

        log.info("Creating new user: {}", user.getUsername());
        return save(user);
    }

    @Override
    public boolean updateUser(User user) {
        // 1. 参数验证
        if (user == null || user.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 2. 检查用户是否存在
        User existingUser = getById(user.getId());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 3. 检查用户名是否重复（排除自己）
        if (StringUtils.hasText(user.getUsername()) && !user.getUsername().equals(existingUser.getUsername())) {
            if (isUsernameExists(user.getUsername())) {
                throw new BusinessException("用户名已存在");
            }
        }

        // 4. 检查邮箱是否重复（排除自己）
        if (StringUtils.hasText(user.getEmail()) && !user.getEmail().equals(existingUser.getEmail())) {
            if (isEmailExists(user.getEmail())) {
                throw new BusinessException("邮箱已存在");
            }
        }

        // 5. 检查手机号是否重复（排除自己）
        if (StringUtils.hasText(user.getPhone()) && !user.getPhone().equals(existingUser.getPhone())) {
            if (isPhoneExists(user.getPhone())) {
                throw new BusinessException("手机号已存在");
            }
        }

        // 6. 如果有新密码，进行加密
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null); // 不更新密码
        }

        log.info("Updating user: {}", user.getUsername());
        return updateById(user);
    }

    @Override
    public boolean disableUser(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        User user = new User();
        user.setId(userId);
        user.setStatus(0); // 设置为禁用

        log.info("Disabling user with ID: {}", userId);
        return updateById(user);
    }

    @Override
    public boolean enableUser(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        User user = new User();
        user.setId(userId);
        user.setStatus(1); // 设置为启用

        log.info("Enabling user with ID: {}", userId);
        return updateById(user);
    }

    @Override
    public IPage<User> getUserList(Page<User> page, String username, String email, Integer status) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 1. 用户名模糊查询
        if (StringUtils.hasText(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        
        // 2. 邮箱模糊查询
        if (StringUtils.hasText(email)) {
            queryWrapper.like(User::getEmail, email);
        }
        
        // 3. 状态精确查询
        if (status != null) {
            queryWrapper.eq(User::getStatus, status);
        }
        
        // 4. 按创建时间倒序排列
        queryWrapper.orderByDesc(User::getCreatedTime);

        return page(page, queryWrapper);
    }

    @Override
    public boolean isUsernameExists(String username) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return count(queryWrapper) > 0;
    }

    @Override
    public boolean isEmailExists(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        return count(queryWrapper) > 0;
    }

    @Override
    public boolean isPhoneExists(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        return count(queryWrapper) > 0;
    }

} 