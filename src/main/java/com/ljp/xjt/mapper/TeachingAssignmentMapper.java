package com.ljp.xjt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljp.xjt.entity.TeachingAssignment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教学安排 Mapper 接口
 * <p>
 * 提供对 teaching_assignments 表的数据库访问能力。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Mapper
public interface TeachingAssignmentMapper extends BaseMapper<TeachingAssignment> {
} 