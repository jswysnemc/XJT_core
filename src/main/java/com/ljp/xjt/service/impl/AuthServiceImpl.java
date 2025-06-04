package com.ljp.xjt.service.impl;

import com.ljp.xjt.common.exception.BusinessException;
import com.ljp.xjt.entity.Role;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.service.AuthService;
import com.ljp.xjt.service.RoleService;
import com.ljp.xjt.service.UserRoleService;
import com.ljp.xjt.service.UserService;
import com.ljp.xjt.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * 认证服务实现类
 * <p>
 * 实现用户认证相关的业务逻辑，包括登录、注册、令牌管理等功能
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（包含JWT令牌）
     */
    @Override
    public LoginResult login(String username, String password) {
        try {
            // 1. 使用Spring Security进行身份验证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 2. 获取用户详情
            User user = userService.findByUsername(username);
            if (user == null) {
                return new LoginResult(false, "用户不存在");
            }
            
            // 3. 获取用户角色
            String role = "";
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                role = user.getRoles().iterator().next().getRoleCode();
            }
            
            // 4. 生成JWT令牌和刷新令牌
            String accessToken = jwtUtils.generateToken(user.getId(), username, role);
            String refreshToken = jwtUtils.generateRefreshToken(user.getId(), username, role);
            
            log.info("User logged in: {}", username);
            return new LoginResult(true, "登录成功", accessToken, refreshToken, user.getId(), username, role);
        } catch (BadCredentialsException e) {
            log.warn("Login failed for user: {}, bad credentials", username);
            return new LoginResult(false, "用户名或密码错误");
        } catch (Exception e) {
            log.error("Login error for user: {}", username, e);
            return new LoginResult(false, "登录失败：" + e.getMessage());
        }
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @param roleCode 角色编码
     * @return 注册结果
     */
    @Override
    @Transactional
    public RegisterResult register(User user, String roleCode) {
        try {
            // 1. 验证用户信息
            if (user == null || !StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
                return new RegisterResult(false, "用户名和密码不能为空");
            }
            
            // 2. 检查用户名是否已存在
            if (userService.isUsernameExists(user.getUsername())) {
                return new RegisterResult(false, "用户名已存在");
            }
            
            // 3. 检查邮箱是否已存在
            if (StringUtils.hasText(user.getEmail()) && userService.isEmailExists(user.getEmail())) {
                return new RegisterResult(false, "邮箱已存在");
            }
            
            // 4. 检查手机号是否已存在
            if (StringUtils.hasText(user.getPhone()) && userService.isPhoneExists(user.getPhone())) {
                return new RegisterResult(false, "手机号已存在");
            }
            
            // 5. 设置用户默认状态和密码加密
            user.setStatus(1); // 1表示启用
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // 6. 保存用户信息
            boolean saved = userService.save(user);
            if (!saved) {
                return new RegisterResult(false, "注册失败");
            }
            
            // 7. 分配角色（如果指定了角色编码）
            if (StringUtils.hasText(roleCode)) {
                boolean roleAssigned = userRoleService.assignRoleByCode(user.getId(), roleCode);
                if (!roleAssigned) {
                    log.warn("Failed to assign role {} to user {}", roleCode, user.getId());
                    // 注册仍然成功，但角色分配失败
                }
            }
            
            log.info("User registered: {}", user.getUsername());
            return new RegisterResult(true, "注册成功", user.getId());
        } catch (Exception e) {
            log.error("Registration error", e);
            return new RegisterResult(false, "注册失败：" + e.getMessage());
        }
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    @Override
    public LoginResult refreshToken(String refreshToken) {
        try {
            // 1. 验证刷新令牌
            if (!jwtUtils.validateToken(refreshToken)) {
                return new LoginResult(false, "刷新令牌无效或已过期");
            }
            
            // 2. 从刷新令牌中获取用户信息
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            Long userId = jwtUtils.getUserIdFromToken(refreshToken);
            String role = jwtUtils.getRoleFromToken(refreshToken);
            
            // 3. 验证用户是否存在
            User user = userService.findByUsername(username);
            if (user == null) {
                return new LoginResult(false, "用户不存在");
            }
            
            // 4. 生成新的访问令牌和刷新令牌
            String newAccessToken = jwtUtils.generateToken(userId, username, role);
            String newRefreshToken = jwtUtils.generateRefreshToken(userId, username, role);
            
            log.info("Token refreshed for user: {}", username);
            return new LoginResult(true, "刷新成功", newAccessToken, newRefreshToken, userId, username, role);
        } catch (Exception e) {
            log.error("Token refresh error", e);
            return new LoginResult(false, "刷新令牌失败：" + e.getMessage());
        }
    }

    /**
     * 用户登出
     *
     * @param token JWT令牌
     * @return 登出结果
     */
    @Override
    public boolean logout(String token) {
        // JWT是无状态的，服务端无需额外处理
        // 如果需要实现服务端控制JWT失效，可以使用黑名单机制
        // 例如，将令牌存入Redis黑名单，并设置过期时间为令牌的剩余有效期
        
        // 这里简单返回成功
        return true;
    }

    /**
     * 验证令牌有效性
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    @Override
    public boolean validateToken(String token) {
        return jwtUtils.validateToken(token);
    }

    /**
     * 从令牌中获取用户信息
     *
     * @param token JWT令牌
     * @return 用户信息
     */
    @Override
    public User getUserFromToken(String token) {
        try {
            String username = jwtUtils.getUsernameFromToken(token);
            return userService.findByUsername(username);
        } catch (Exception e) {
            log.error("Error getting user from token", e);
            return null;
        }
    }
} 