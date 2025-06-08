package com.ljp.xjt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ljp.xjt.common.exception.BusinessException;
import com.ljp.xjt.config.FileStorageProperties;
import com.ljp.xjt.entity.Avatar;
import com.ljp.xjt.mapper.AvatarMapper;
import com.ljp.xjt.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final Path fileStorageLocation;
    private final AvatarMapper avatarMapper;

    @Autowired
    public FileServiceImpl(FileStorageProperties fileStorageProperties, AvatarMapper avatarMapper) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadPath()).toAbsolutePath().normalize();
        this.avatarMapper = avatarMapper;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new BusinessException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public Avatar storeAvatar(MultipartFile file, Long userId) {
        // 1. 标准化和生成文件名
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        try {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } catch (Exception e) {
            throw new BusinessException("Failed to extract file extension from file " + originalFilename);
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;


        try {
            // 2. 检查文件名是否包含无效字符
            if (fileName.contains("..")) {
                throw new BusinessException("Sorry! Filename contains invalid path sequence " + originalFilename);
            }

            // 3. 将文件复制到目标位置
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 4. 在数据库中创建记录
            Avatar avatar = new Avatar();
            avatar.setUserId(userId);
            avatar.setFileName(fileName);
            avatar.setOriginalName(originalFilename);
            avatar.setFilePath(targetLocation.toString());
            avatar.setFileSize(file.getSize());
            avatar.setContentType(file.getContentType());
            avatar.setCreatedTime(LocalDateTime.now());
            avatarMapper.insert(avatar);

            return avatar;
        } catch (IOException ex) {
            throw new BusinessException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new BusinessException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new BusinessException("File not found " + fileName, ex);
        }
    }

    @Override
    public Avatar findLatestAvatarByUserId(Long userId) {
        return avatarMapper.selectOne(new QueryWrapper<Avatar>()
                .eq("user_id", userId)
                .orderByDesc("created_time")
                .last("LIMIT 1"));
    }
} 