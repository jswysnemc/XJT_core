package com.ljp.xjt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户实体类
 * <p>
 * 对应数据库表：users
 * 存储系统中所有用户的基本信息，包括登录凭据和基本个人信息
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;                    // 用户唯一标识

    @TableField("username")
    private String username;            // 用户名，登录标识

    @TableField("password")
    private String password;            // 密码，BCrypt加密

    @TableField("email")
    private String email;               // 邮箱地址

    @TableField("phone")
    private String phone;               // 手机号码

    @TableField("status")
    private Integer status;             // 账号状态：0-禁用，1-正常

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;  // 创建时间

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;  // 更新时间

    /**
     * 用户角色集合
     * <p>
     * 通过中间表 user_roles 关联，一个用户可以有多个角色。
     * 此字段不直接映射到users表的列。
     * </p>
     */
    @TableField(exist = false)
    private Set<Role> roles;            // 用户拥有的角色集合

} 