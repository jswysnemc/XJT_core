package com.ljp.xjt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传配置属性类
 * <p>
 * 绑定 application.yml 中的 app.file 配置
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.file")
public class FileStorageProperties {

    /**
     * 文件上传存储目录
     */
    private String uploadPath;

    /**
     * 最大文件大小
     */
    private String maxSize;

    /**
     * 允许的文件类型
     */
    private String[] allowedTypes;
} 