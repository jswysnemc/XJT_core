package com.ljp.xjt.controller;

import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.dto.LoginRequest;
import com.ljp.xjt.dto.LoginResponse;
import com.ljp.xjt.dto.RefreshTokenRequest;
import com.ljp.xjt.dto.RefreshTokenResponse;
import com.ljp.xjt.dto.RegisterRequest;
import com.ljp.xjt.dto.ValidateTokenResponse;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.service.AuthService;
import com.ljp.xjt.service.RoleService;
import com.ljp.xjt.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 认证控制器
 * <p>
 * 提供用户登录认证、注册和令牌刷新相关的API接口。
 * </p>
 * 
 * @author ljp
 * @version 1.1
 * @since 2025-05-30
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AuthService authService;
    private final RoleService roleService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求体，包含用户名和密码
     * @return 包含JWT的登录响应
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户使用用户名和密码进行登录认证，成功后返回JWT")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Attempting login for user: {}", loginRequest.getUsername());

        // 调用认证服务进行登录
        AuthService.LoginResult loginResult = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        
        if (!loginResult.isSuccess()) {
            return ApiResponse.error(loginResult.getMessage());
        }
        
        // 构建登录响应
        LoginResponse response = new LoginResponse(
            loginResult.getAccessToken(),
            loginResult.getRefreshToken(),
            loginResult.getUserId(),
            loginResult.getUsername(),
            loginResult.getRole()
        );

        log.info("User: {} logged in successfully.", loginRequest.getUsername());
        return ApiResponse.success("登录成功", response);
    }
    
    /**
     * 刷新令牌
     *
     * @param refreshTokenRequest 刷新令牌请求体
     * @return 包含新JWT的响应
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public ApiResponse<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Attempting to refresh token");
        
        // 调用认证服务刷新令牌
        AuthService.LoginResult refreshResult = authService.refreshToken(refreshTokenRequest.getRefreshToken());
        
        if (!refreshResult.isSuccess()) {
            return ApiResponse.error(refreshResult.getMessage());
    }
    
        // 构建刷新令牌响应
        RefreshTokenResponse response = new RefreshTokenResponse(
            refreshResult.getAccessToken(),
            refreshResult.getRefreshToken()
        );
        
        log.info("Token refreshed successfully for user: {}", refreshResult.getUsername());
        return ApiResponse.success("令牌刷新成功", response);
    }
    
    /**
     * 验证令牌有效性
     *
     * @param token JWT令牌
     * @return 包含令牌验证结果的响应
     */
    @GetMapping("/validate-token")
    @Operation(summary = "验证令牌有效性", description = "验证JWT令牌是否有效，并返回详细信息")
    public ApiResponse<ValidateTokenResponse> validateToken(
            @Parameter(description = "JWT令牌 (Bearer Token或直接传入token)") 
            @RequestParam String token) {
        log.info("Validating token");
        
        // 如果token以"Bearer "开头，则去除前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 验证令牌是否有效
        boolean isValid = jwtUtils.validateToken(token);
        
        if (!isValid) {
            log.warn("Invalid token provided");
            ValidateTokenResponse response = new ValidateTokenResponse(
                false, null, null, null, null, null, 0L);
            return ApiResponse.success("令牌无效", response);
        }
        
        try {
            // 获取令牌中的信息
            String username = jwtUtils.getUsernameFromToken(token);
            Long userId = jwtUtils.getUserIdFromToken(token);
            String role = jwtUtils.getRoleFromToken(token);
            LocalDateTime expiresAt = jwtUtils.dateToLocalDateTime(jwtUtils.getExpirationDateFromToken(token));
            LocalDateTime issuedAt = jwtUtils.dateToLocalDateTime(jwtUtils.getIssuedAtFromToken(token));
            Long remainingSeconds = jwtUtils.getRemainingTimeFromToken(token);
            
            // 构建响应
            ValidateTokenResponse response = new ValidateTokenResponse(
                true, userId, username, role, issuedAt, expiresAt, remainingSeconds);
            
            log.info("Token validated successfully for user: {}", username);
            return ApiResponse.success("令牌有效", response);
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return ApiResponse.error("令牌验证过程中发生错误");
        }
    }
    
    /**
     * 用户注册
     *
     * @param registerRequest 注册请求体
     * @return 注册结果
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册，成功后返回用户ID")
    public ApiResponse<Long> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Attempting to register user: {}", registerRequest.getUsername());
        
        // 1. 验证密码确认
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return ApiResponse.error("两次输入的密码不一致");
        }
        
        // 2. 构建用户实体
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setStatus(1); // 默认启用
        
        // 3. 调用认证服务进行注册
        AuthService.RegisterResult registerResult = authService.register(
            user, 
            registerRequest.getRoleType(), 
            registerRequest.getStudentNumber()
        );
        
        if (!registerResult.isSuccess()) {
            return ApiResponse.error(registerResult.getMessage());
        }
        
        log.info("User registered successfully: {}", registerRequest.getUsername());
        return ApiResponse.success("注册成功", registerResult.getUserId());
    }
} 