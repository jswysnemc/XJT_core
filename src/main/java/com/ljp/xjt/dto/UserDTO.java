package com.ljp.xjt.dto;

import com.ljp.xjt.entity.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户信息数据传输对象
 * <p>
 * 用于在API响应中安全地传输用户信息，不包含密码等敏感字段，并可附加角色等关联信息。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-11
 */
@Data
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Set<Role> roles;
} 