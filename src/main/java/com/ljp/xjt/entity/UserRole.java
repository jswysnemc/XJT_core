package com.ljp.xjt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联实体类
 * <p>
 * 对应数据库表：user_roles
 * 存储用户与角色的多对多关系
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_roles")
public class UserRole {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;                    // 关联记录唯一标识

    @TableField("user_id")
    private Long userId;                // 用户ID

    @TableField("role_id")
    private Long roleId;                // 角色ID
} 