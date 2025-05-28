package com.ljp.xjt.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis Plus配置类
 * <p>
 * 配置MyBatis Plus的基础功能
 * 提供自动填充功能，自动设置创建时间、更新时间等字段
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-26
 */
@Configuration
@MapperScan("com.ljp.xjt.mapper")
public class MybatisPlusConfig {

    /**
     * 自动填充功能配置
     * 自动填充创建时间、更新时间、逻辑删除标记等字段
     *
     * @return 元数据对象处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            /**
             * 插入时自动填充
             *
             * @param metaObject 元对象
             */
            @Override
            public void insertFill(MetaObject metaObject) {
                // 1. 自动填充创建时间
                this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, LocalDateTime.now());
                
                // 2. 自动填充更新时间
                this.strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
                
                // 3. 自动填充逻辑删除标记
                this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
            }

            /**
             * 更新时自动填充
             *
             * @param metaObject 元对象
             */
            @Override
            public void updateFill(MetaObject metaObject) {
                // 1. 自动填充更新时间
                this.strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }

} 