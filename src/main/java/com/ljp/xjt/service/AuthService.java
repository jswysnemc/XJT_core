package com.ljp.xjt.service;

import com.ljp.xjt.entity.User;

/**
 * 认证服务接口
 * <p>
 * 提供用户认证相关的业务操作，包括登录、注册、令牌管理等功能
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（包含JWT令牌）
     */
    LoginResult login(String username, String password);

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @param roleCode 角色编码
     * @return 注册结果
     */
    RegisterResult register(User user, String roleCode);

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    LoginResult refreshToken(String refreshToken);

    /**
     * 用户登出
     *
     * @param token JWT令牌
     * @return 登出结果
     */
    boolean logout(String token);

    /**
     * 验证令牌有效性
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 从令牌中获取用户信息
     *
     * @param token JWT令牌
     * @return 用户信息
     */
    User getUserFromToken(String token);

    /**
     * 登录结果类
     */
    class LoginResult {
        private boolean success;
        private String message;
        private String accessToken;
        private String refreshToken;
        private Long userId;
        private String username;
        private String role;

        public LoginResult() {}

        public LoginResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public LoginResult(boolean success, String message, String accessToken, String refreshToken, 
                          Long userId, String username, String role) {
            this.success = success;
            this.message = message;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.userId = userId;
            this.username = username;
            this.role = role;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    /**
     * 注册结果类
     */
    class RegisterResult {
        private boolean success;
        private String message;
        private Long userId;

        public RegisterResult() {}

        public RegisterResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public RegisterResult(boolean success, String message, Long userId) {
            this.success = success;
            this.message = message;
            this.userId = userId;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }

} 