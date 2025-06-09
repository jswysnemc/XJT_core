package com.ljp.xjt.security;

import com.ljp.xjt.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 自定义Spring Security用户详情类
 * <p>
 * 继承自Spring Security的User类，并额外封装了我们自己的User实体，
 * 以便在认证成功后可以在任何地方方便地获取完整的用户信息。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-09
 */
@Getter
public class SecurityUser extends org.springframework.security.core.userdetails.User {

    /**
     * 我们自己的用户实体
     */
    private final User user;

    /**
     * 构造函数
     *
     * @param user        自定义的用户实体
     * @param authorities 权限集合
     */
    public SecurityUser(User user, Collection<? extends GrantedAuthority> authorities) {
        // 调用父类构造函数，传入用户名、密码和权限等核心信息
        super(user.getUsername(), user.getPassword(), user.getStatus() == 1, true, true, true, authorities);
        // 保存我们自己的User实体
        this.user = user;
    }
} 