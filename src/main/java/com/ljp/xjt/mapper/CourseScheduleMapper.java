package com.ljp.xjt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljp.xjt.entity.CourseSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 课程安排Mapper接口
 * <p>
 * 提供课程安排数据访问方法
 * </p>
 * 
 * @author ljp
 * @version 1.0
 * @since 2025-05-30
 */
@Mapper
public interface CourseScheduleMapper extends BaseMapper<CourseSchedule> {
    
} 