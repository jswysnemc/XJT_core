package com.ljp.xjt.controller;

import com.ljp.xjt.common.ApiResponse;
import com.ljp.xjt.common.exception.BusinessException;
import com.ljp.xjt.dto.AvatarUploadResponse;
import com.ljp.xjt.dto.PasswordChangeRequest;
import com.ljp.xjt.dto.ProfileDto;
import com.ljp.xjt.entity.Avatar;
import com.ljp.xjt.entity.User;
import com.ljp.xjt.service.FileService;
import com.ljp.xjt.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/profile")
@Slf4j
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    /**
     * 获取当前登录用户的个人信息
     *
     * @return 个人信息DTO
     */
    @GetMapping
    public ApiResponse<ProfileDto> getProfile() {
        User currentUser = getCurrentUser();
        User userDetails = userService.findById(currentUser.getId());
        
        ProfileDto profileDto = new ProfileDto();
        profileDto.setUserId(userDetails.getId());
        profileDto.setUsername(userDetails.getUsername());
        profileDto.setEmail(userDetails.getEmail());
        profileDto.setPhone(userDetails.getPhone());
        
        // 设置角色
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        profileDto.setRoles(authentication.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()));

        // 设置头像URL
        Avatar avatar = fileService.findLatestAvatarByUserId(userDetails.getId());
        if (avatar != null) {
            String avatarUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/v1/profile/avatar/")
                    .path(avatar.getFileName())
                    .toUriString();
            profileDto.setAvatarUrl(avatarUrl);
        }

        return ApiResponse.success(profileDto);
    }

    /**
     * 修改密码
     *
     * @param request 密码修改请求
     * @return a
     */
    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("新密码和确认密码不匹配");
        }
        User currentUser = getCurrentUser();
        userService.changePassword(currentUser, request.getOldPassword(), request.getNewPassword());
        return ApiResponse.success("密码修改成功");
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return a
     */
    @PostMapping("/avatar")
    public ApiResponse<AvatarUploadResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        User currentUser = getCurrentUser();
        Avatar avatar = fileService.storeAvatar(file, currentUser.getId());

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/v1/profile/avatar/")
                .path(avatar.getFileName())
                .toUriString();

        AvatarUploadResponse response = new AvatarUploadResponse(avatar.getFileName(), fileDownloadUri, file.getContentType(), file.getSize());
        return ApiResponse.success(response);
    }
    
    /**
     * 获取头像
     *
     * @param fileName 文件名
     * @param request  a
     * @return 头像资源
     */
    @GetMapping("/avatar/{fileName:.+}")
    public ResponseEntity<Resource> getAvatar(@PathVariable("fileName") String fileName, HttpServletRequest request) {
        Resource resource = fileService.loadFileAsResource(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


    /**
     * 获取当前登录用户实体
     *
     * @return User
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("当前用户未登录或认证失败");
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }
} 