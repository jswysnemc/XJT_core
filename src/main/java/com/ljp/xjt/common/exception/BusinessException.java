package com.ljp.xjt.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 用于处理业务逻辑中的各种异常情况，如数据验证失败、业务规则冲突等
 * 继承RuntimeException，支持异常链和自定义错误码
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;         // 错误码
    private final String message;   // 错误消息

    /**
     * 构造业务异常（默认错误码500）
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    /**
     * 构造业务异常（自定义错误码）
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造业务异常（带原因）
     *
     * @param message 错误消息
     * @param cause 原始异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
        this.message = message;
    }

    /**
     * 构造业务异常（完整参数）
     *
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原始异常
     */
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

} 