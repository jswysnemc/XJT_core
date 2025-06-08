package com.ljp.xjt.service;

import com.ljp.xjt.entity.Avatar;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

/**
 * <p>
 * 文件服务类
 * </p>
 *
 * @author ljp
 * @since 2025-05-30
 */
public interface FileService {
    /**
     * 存储文件
     *
     * @param file   MultipartFile
     * @param userId 用户ID
     * @return 存储的头像实体
     */
    Avatar storeAvatar(MultipartFile file, Long userId);

    /**
     * 加载文件资源
     *
     * @param filename 文件名
     * @return Resource
     */
    Resource loadFileAsResource(String filename);

    /**
     * 获取用户最新的头像
     * @param userId
     * @return
     */
    Avatar findLatestAvatarByUserId(Long userId);
} 