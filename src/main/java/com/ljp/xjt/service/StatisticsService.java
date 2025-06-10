package com.ljp.xjt.service;

import com.ljp.xjt.dto.ClassGradeAnalysisDTO;

/**
 * 统计服务接口
 * <p>
 * 提供各类数据统计与分析的业务逻辑。
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
public interface StatisticsService {

    /**
     * 获取指定班级在某一门课程上的详细成绩统计和分布数据
     *
     * @param classId  班级ID
     * @param courseId 课程ID
     * @return ClassGradeAnalysisDTO 包含完整分析数据的DTO
     */
    ClassGradeAnalysisDTO getClassGradeAnalysis(Long classId, Long courseId);

} 