package com.ljp.xjt.service.impl;

import com.ljp.xjt.entity.User;
import com.ljp.xjt.service.UserService;
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

    private final UserService userService;

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
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户 '" + username + "' 未找到");
        }

        // 2. 构建权限集合 (这里暂时使用一个固定的"ROLE_USER"，后续应根据用户实际角色动态构建)
        // Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        // TODO: 将来需要从用户角色表中获取角色信息，并转换为GrantedAuthority
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName().toUpperCase()))
                    .collect(Collectors.toSet());
        } else {
            // 如果用户没有分配任何角色，可以赋予一个默认角色，或者根据业务需求处理
            // authorities.add(new SimpleGrantedAuthority("ROLE_DEFAULT"));
            // 或者，如果要求用户必须有角色才能登录，可以在这里抛出异常或返回null/特定的UserDetails对象
             throw new UsernameNotFoundException("用户 '" + username + "' 未分配任何角色");
        }


        // 3. 创建Spring Security的UserDetails对象
        //    参数分别为：用户名，密码，是否启用，账户是否未过期，凭证是否未过期，账户是否未锁定，权限列表
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1, // 1表示启用
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
} 