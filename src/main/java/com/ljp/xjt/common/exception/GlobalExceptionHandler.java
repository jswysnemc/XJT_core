package com.ljp.xjt.common.exception;

import com.ljp.xjt.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一处理系统中的各种异常，包括业务异常、参数验证异常、权限异常等
 * 确保所有异常都能返回统一格式的错误响应
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Object> handleBusinessException(BusinessException e) {
        log.warn("Business exception occurred: {}", e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常（@Valid注解验证失败）
     *
     * @param e 参数验证异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 1. 获取所有字段验证错误信息
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("Parameter validation failed: {}", errorMessage);
        return ApiResponse.error(400, "参数验证失败: " + errorMessage);
    }

    /**
     * 处理绑定异常
     *
     * @param e 绑定异常
     * @return 错误响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleBindException(BindException e) {
        // 1. 获取所有字段验证错误信息
        String errorMessage = e.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("Parameter binding failed: {}", errorMessage);
        return ApiResponse.error(400, "参数绑定失败: " + errorMessage);
    }

    /**
     * 处理约束违反异常
     *
     * @param e 约束违反异常
     * @return 错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleConstraintViolationException(ConstraintViolationException e) {
        // 1. 获取所有约束违反信息
        String errorMessage = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("Constraint validation failed: {}", errorMessage);
        return ApiResponse.error(400, "约束验证失败: " + errorMessage);
    }

    /**
     * 处理权限不足异常
     *
     * @param e 权限异常
     * @return 错误响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Object> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return ApiResponse.forbidden();
    }

    /**
     * 处理认证失败异常
     *
     * @param e 认证异常
     * @return 错误响应
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Object> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("Authentication failed: {}", e.getMessage());
        return ApiResponse.error(401, "用户名或密码错误");
    }

    /**
     * 处理文件上传大小超限异常
     *
     * @param e 文件上传异常
     * @return 错误响应
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("File upload size exceeded: {}", e.getMessage());
        return ApiResponse.error(400, "文件上传大小超过限制");
    }

    /**
     * 处理IllegalArgumentException异常
     *
     * @param e 非法参数异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        return ApiResponse.error(400, "参数错误: " + e.getMessage());
    }

    /**
     * 处理所有未捕获的异常
     *
     * @param e 未知异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleException(Exception e) {
        log.error("Unexpected error occurred", e);
        return ApiResponse.error(500, "系统内部错误，请联系管理员");
    }

} 