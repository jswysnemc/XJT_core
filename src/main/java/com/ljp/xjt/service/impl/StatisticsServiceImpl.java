package com.ljp.xjt.service.impl;

import com.ljp.xjt.common.exception.BusinessException;
import com.ljp.xjt.dto.ClassGradeAnalysisDTO;
import com.ljp.xjt.dto.GradeDistributionBucketDTO;
import com.ljp.xjt.dto.GradeStatisticsDTO;
import com.ljp.xjt.entity.Classes;
import com.ljp.xjt.entity.Course;
import com.ljp.xjt.entity.Grade;
import com.ljp.xjt.entity.Student;
import com.ljp.xjt.mapper.ClassesMapper;
import com.ljp.xjt.mapper.CourseMapper;
import com.ljp.xjt.mapper.GradeMapper;
import com.ljp.xjt.mapper.StudentMapper;
import com.ljp.xjt.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-10
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final ClassesMapper classesMapper;
    private final CourseMapper courseMapper;
    private final StudentMapper studentMapper;
    private final GradeMapper gradeMapper;

    @Override
    public ClassGradeAnalysisDTO getClassGradeAnalysis(Long classId, Long courseId) {
        // 1. 验证班级和课程是否存在
        Classes classes = classesMapper.selectById(classId);
        if (classes == null) {
            throw new BusinessException("班级不存在");
        }
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        // 2. 获取班级总人数
        long totalStudentCount = studentMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Student>().eq("class_id", classId)
        );

        // 3. 获取所有相关成绩
        List<Grade> grades = gradeMapper.findGradesByClassAndCourse(classId, courseId);
        List<Double> scores = grades.stream().map(g -> g.getScore().doubleValue()).collect(Collectors.toList());
        long evaluatedStudentCount = scores.size();

        // 4. 计算核心统计指标
        GradeStatisticsDTO statistics = calculateStatistics(scores, totalStudentCount, evaluatedStudentCount);

        // 5. 计算成绩分布
        List<GradeDistributionBucketDTO> distribution = calculateDistribution(scores);

        // 6. 组装最终DTO
        ClassGradeAnalysisDTO analysisDTO = new ClassGradeAnalysisDTO();
        analysisDTO.setClassId(classId);
        analysisDTO.setClassName(classes.getClassName());
        analysisDTO.setCourseId(courseId);
        analysisDTO.setCourseName(course.getCourseName());
        analysisDTO.setStatistics(statistics);
        analysisDTO.setDistribution(distribution);

        return analysisDTO;
    }

    private GradeStatisticsDTO calculateStatistics(List<Double> scores, long totalStudentCount, long evaluatedStudentCount) {
        if (scores.isEmpty()) {
            return GradeStatisticsDTO.builder()
                    .totalStudentCount(totalStudentCount)
                    .evaluatedStudentCount(evaluatedStudentCount)
                    .averageScore(0).highestScore(0).lowestScore(0).passingRate(0)
                    .build();
        }

        DoubleSummaryStatistics summary = scores.stream().mapToDouble(Double::doubleValue).summaryStatistics();
        long passingCount = scores.stream().filter(s -> s >= 60.0).count();
        double passingRate = (evaluatedStudentCount > 0) ? (double) passingCount / evaluatedStudentCount : 0.0;

        return GradeStatisticsDTO.builder()
                .totalStudentCount(totalStudentCount)
                .evaluatedStudentCount(evaluatedStudentCount)
                .averageScore(round(summary.getAverage()))
                .highestScore(summary.getMax())
                .lowestScore(summary.getMin())
                .passingRate(round(passingRate))
                .build();
    }

    private List<GradeDistributionBucketDTO> calculateDistribution(List<Double> scores) {
        long excellent = scores.stream().filter(s -> s >= 90).count();
        long good = scores.stream().filter(s -> s >= 80 && s < 90).count();
        long medium = scores.stream().filter(s -> s >= 70 && s < 80).count();
        long pass = scores.stream().filter(s -> s >= 60 && s < 70).count();
        long fail = scores.stream().filter(s -> s < 60).count();

        List<GradeDistributionBucketDTO> distribution = new ArrayList<>();
        distribution.add(new GradeDistributionBucketDTO("90-100分 (优秀)", excellent));
        distribution.add(new GradeDistributionBucketDTO("80-89分 (良好)", good));
        distribution.add(new GradeDistributionBucketDTO("70-79分 (中等)", medium));
        distribution.add(new GradeDistributionBucketDTO("60-69分 (及格)", pass));
        distribution.add(new GradeDistributionBucketDTO("<60分 (不及格)", fail));

        return distribution;
    }
    
    private double round(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
} 