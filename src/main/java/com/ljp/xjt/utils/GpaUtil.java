package com.ljp.xjt.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * GPA计算工具类
 * <p>
 * 提供根据分数计算绩点(GPA)的静态方法
 * </p>
 *
 * @author ljp
 * @version 1.0
 * @since 2025-06-11
 */
public class GpaUtil {

    private static final BigDecimal PASS_SCORE = new BigDecimal("60");

    /**
     * 根据百分制分数计算GPA（4.0分制）
     * <p>
     * 计算规则:
     * - 90分及以上: 4.0
     * - 85-89分: 3.7
     * - 82-84分: 3.3
     * - 78-81分: 3.0
     * - 75-77分: 2.7
     * - 72-74分: 2.3
     * - 68-71分: 2.0
     * - 65-67分: 1.7
     * - 60-64分: 1.0
     * - 60分以下: 0.0
     *
     * @param score 分数
     * @return 对应的GPA
     */
    public static BigDecimal calculateGpa(BigDecimal score) {
        if (score == null) {
            return BigDecimal.ZERO;
        }
        if (score.compareTo(new BigDecimal("90")) >= 0) {
            return new BigDecimal("4.0");
        } else if (score.compareTo(new BigDecimal("85")) >= 0) {
            return new BigDecimal("3.7");
        } else if (score.compareTo(new BigDecimal("82")) >= 0) {
            return new BigDecimal("3.3");
        } else if (score.compareTo(new BigDecimal("78")) >= 0) {
            return new BigDecimal("3.0");
        } else if (score.compareTo(new BigDecimal("75")) >= 0) {
            return new BigDecimal("2.7");
        } else if (score.compareTo(new BigDecimal("72")) >= 0) {
            return new BigDecimal("2.3");
        } else if (score.compareTo(new BigDecimal("68")) >= 0) {
            return new BigDecimal("2.0");
        } else if (score.compareTo(new BigDecimal("65")) >= 0) {
            return new BigDecimal("1.7");
        } else if (score.compareTo(new BigDecimal("60")) >= 0) {
            return new BigDecimal("1.0");
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 判断成绩是否及格
     *
     * @param score 分数
     * @return 如果分数大于等于60，则返回true，否则返回false
     */
    public static boolean isScoreNormal(BigDecimal score) {
        return score != null && score.compareTo(PASS_SCORE) >= 0;
    }
} 