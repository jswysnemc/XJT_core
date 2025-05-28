package com.ljp.xjt.controller;

import com.ljp.xjt.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * <p>
 * 提供系统健康检查和基础测试接口
 * 用于验证项目启动状态和基础功能是否正常
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Slf4j
@RestController
@RequestMapping("/test")
@Tag(name = "测试接口", description = "系统测试和健康检查接口")
public class TestController {

    /**
     * 系统健康检查
     *
     * @return 系统状态信息
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    public ApiResponse<Map<String, Object>> health() {
        log.info("System health check requested");
        
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("message", "学生成绩管理系统运行正常");
        healthInfo.put("version", "1.0.0");
        
        return ApiResponse.success("系统健康检查通过", healthInfo);
    }

    /**
     * 获取系统信息
     *
     * @return 系统基本信息
     */
    @GetMapping("/info")
    @Operation(summary = "系统信息", description = "获取系统基本配置信息")
    public ApiResponse<Map<String, Object>> info() {
        log.info("System info requested");
        
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("applicationName", "学生成绩管理系统");
        systemInfo.put("version", "1.0.0");
        systemInfo.put("author", "ljp");
        systemInfo.put("springBootVersion", "3.5.0");
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("startTime", LocalDateTime.now());
        
        return ApiResponse.success("系统信息获取成功", systemInfo);
    }

    /**
     * API响应格式测试
     *
     * @return 标准API响应格式示例
     */
    @GetMapping("/response-format")
    @Operation(summary = "响应格式测试", description = "展示标准API响应格式")
    public ApiResponse<Map<String, Object>> responseFormat() {
        log.info("Response format test requested");
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "这是一个标准的API响应格式示例");
        data.put("code", 200);
        data.put("success", true);
        data.put("timestamp", LocalDateTime.now());
        
        return ApiResponse.success("响应格式测试成功", data);
    }

} 