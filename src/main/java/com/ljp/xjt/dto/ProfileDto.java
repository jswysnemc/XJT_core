package com.ljp.xjt.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * <p>
 * 用户个人信息 DTO
 * </p>
 *
 * @author ljp
 * @since 2025-05-30
 */
@Data
@NoArgsConstructor
public class ProfileDto {
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String avatarUrl;
    private List<String> roles;

    // 可以根据需要添加特定角色的信息
    // private StudentProfileDto studentInfo;
    // private TeacherProfileDto teacherInfo;
    // private AdminProfileDto adminInfo;
} 