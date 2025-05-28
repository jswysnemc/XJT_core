package com.ljp.xjt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 角色实体类
 * <p>
 * 对应数据库表：roles
 * 定义系统中的角色类型，如学生、教师、管理员等
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("roles")
public class Role {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;                    // 角色唯一标识

    @TableField("role_name")
    private String roleName;            // 角色名称

    @TableField("role_code")
    private String roleCode;            // 角色编码

    @TableField("description")
    private String description;         // 角色描述

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;  // 创建时间

} 