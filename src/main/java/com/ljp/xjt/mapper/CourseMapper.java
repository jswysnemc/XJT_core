package com.ljp.xjt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljp.xjt.entity.Course;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程数据访问层接口
 * <p>
 * 继承自MyBatis Plus的BaseMapper，提供基础的CRUD操作。
 * 包含对 `courses` 表的数据库操作方法。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-05-29
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    // 如果需要自定义SQL查询，可以在这里添加方法声明
    // 例如根据课程编码查询课程信息等

} 