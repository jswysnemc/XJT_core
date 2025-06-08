package com.ljp.xjt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 头像上传响应 DTO
 * </p>
 *
 * @author ljp
 * @since 2025-05-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvatarUploadResponse {

    /**
     * 新头像的文件名
     */
    private String fileName;

    /**
     * 新头像的访问URL
     */
    private String fileDownloadUri;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小
     */
    private long size;
} 