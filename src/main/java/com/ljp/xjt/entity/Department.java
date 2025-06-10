package com.ljp.xjt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 部门实体类
 * <p>
 *     对应数据库中的 departments 表
 * </p>
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("departments")
public class Department {

    /**
     * 部门ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 部门编码
     */
    @TableField("dept_code")
    private String deptCode;

    /**
     * 部门描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
} 