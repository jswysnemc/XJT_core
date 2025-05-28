package com.ljp.xjt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 学生成绩管理系统启动类
 * <p>
 * 基于Spring Boot 3.5.0框架的学生成绩管理系统主启动类
 * 提供学生、教师、管理员三种角色的权限管理和成绩管理功能
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@SpringBootApplication
public class StudentGradeSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentGradeSystemApplication.class, args);
        System.out.println("=====================================");
        System.out.println("学生成绩管理系统启动成功！");
        System.out.println("API文档地址: http://localhost:8080/api/doc.html");
        System.out.println("=====================================");
    }

} 