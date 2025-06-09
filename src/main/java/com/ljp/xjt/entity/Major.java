package com.ljp.xjt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 专业信息表
 * </p>
 *
 * @author ljp
 * @since 2025-06-09
 */
@Data
@TableName("majors")
public class Major implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String majorName;

    private String majorCode;

    private Long departmentId;

    private String description;

    private LocalDateTime createdTime;
} 