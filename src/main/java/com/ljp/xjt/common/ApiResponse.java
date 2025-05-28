package com.ljp.xjt.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API响应数据模型
 * <p>
 * 封装了API请求的响应结果，包含状态码、消息和数据体
 * 使用泛型T表示数据体的类型，以适应不同类型的响应数据
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 * 
 * @param <T> 响应数据的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int code;           // 响应状态码，200/201表示成功，其他值表示错误
    private String message;     // 响应消息，成功或错误的描述信息
    private T data;             // 响应数据，包含实际返回的数据内容
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp; // 响应时间戳

    /**
     * 判断API请求是否成功
     *
     * @return 如果状态码为200或201则返回true，否则返回false
     */
    public boolean isSuccess() {
        return code == 200 || code == 201;
    }

    /**
     * 创建成功响应（无数据）
     *
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "操作成功", null, LocalDateTime.now());
    }

    /**
     * 创建成功响应（带数据）
     *
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data, LocalDateTime.now());
    }

    /**
     * 创建成功响应（自定义消息和数据）
     *
     * @param message 自定义消息
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, LocalDateTime.now());
    }

    /**
     * 创建失败响应
     *
     * @param code 错误状态码
     * @param message 错误消息
     * @return 失败响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null, LocalDateTime.now());
    }

    /**
     * 创建失败响应（默认500状态码）
     *
     * @param message 错误消息
     * @return 失败响应对象
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null, LocalDateTime.now());
    }

    /**
     * 创建创建成功响应（201状态码）
     *
     * @param data 创建的数据
     * @return 创建成功响应对象
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "创建成功", data, LocalDateTime.now());
    }

    /**
     * 创建权限不足响应（403状态码）
     *
     * @return 权限不足响应对象
     */
    public static <T> ApiResponse<T> forbidden() {
        return new ApiResponse<>(403, "权限不足", null, LocalDateTime.now());
    }

    /**
     * 创建未授权响应（401状态码）
     *
     * @return 未授权响应对象
     */
    public static <T> ApiResponse<T> unauthorized() {
        return new ApiResponse<>(401, "未授权", null, LocalDateTime.now());
    }

    /**
     * 创建资源未找到响应（404状态码）
     *
     * @return 资源未找到响应对象
     */
    public static <T> ApiResponse<T> notFound() {
        return new ApiResponse<>(404, "资源未找到", null, LocalDateTime.now());
    }

} 