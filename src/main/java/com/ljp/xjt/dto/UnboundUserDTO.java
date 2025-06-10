package com.ljp.xjt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 未绑定学生的用户信息数据传输对象
 * <p>
 * 用于管理员查询未绑定任何学生记录的用户列表。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnboundUserDTO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;
} 