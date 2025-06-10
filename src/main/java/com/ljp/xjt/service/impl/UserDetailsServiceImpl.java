package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljp.xjt.entity.Role;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.mapper.RoleMapper;
import com.ljp.xjt.mapper.UserMapper;
import com.ljp.xjt.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户详情服务实现类
 * <p>
 * 实现Spring Security的UserDetailsService接口，用于从数据库加载用户认证信息。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    /**
     * 根据用户名加载用户信息
     * <p>
     * 此方法在用户尝试登录时由Spring Security调用。
     * 它会查询数据库以获取用户的详细信息，包括密码和权限。
     * </p>
     *
     * @param username 用户名
     * @return UserDetails对象，包含用户的认证和授权信息
     * @throws UsernameNotFoundException 如果用户未找到，则抛出此异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 根据用户名从数据库查询用户信息
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户 '" + username + "' 未找到");
        }

        // 2. 查询并设置用户的角色信息
        Set<Role> roles = roleMapper.findRolesByUserId(user.getId());
        user.setRoles(roles);

        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleCode().toUpperCase()))
                    .collect(Collectors.toSet());
        } else {
            // 如果用户没有分配任何角色，可以赋予一个默认角色，或者根据业务需求处理
            // authorities.add(new SimpleGrantedAuthority("ROLE_DEFAULT"));
            // 或者，如果要求用户必须有角色才能登录，可以在这里抛出异常或返回null/特定的UserDetails对象
             throw new UsernameNotFoundException("用户 '" + username + "' 未分配任何角色");
        }


        // 3. 创建并返回自定义的SecurityUser对象
        //    这将我们的User实体封装到Spring Security的UserDetails中
        return new SecurityUser(user, authorities);
    }
} 