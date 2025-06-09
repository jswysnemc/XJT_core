package com.ljp.xjt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生个人信息数据传输对象
 * <p>
 * 用于封装学生查询个人信息时返回的组合数据，
 * 包含了学生（student）和用户（user）两个表的字段。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Data
public class StudentProfileDTO {

    // --- User Fields ---
    private String username;
    private String email;
    private String phone;

    // --- Student Fields ---
    private Long id;
    private Long userId;
    private String studentNumber;
    private String studentName;
    private Integer gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private Long classId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
} 