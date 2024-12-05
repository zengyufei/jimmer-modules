package com.zyf.common.utils

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * BigDecimal 工具类
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2018/01/17 13:54
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
object SmartBigDecimalUtil {
    /**
     * 金额 保留小数点 2
     */
    const val AMOUNT_DECIMAL_POINT: Int = 2

    val ONE_HUNDRED: BigDecimal = BigDecimal("100")

    /**
     * BigDecimal 加法 num1 + num2
     * 未做非空校验
     *
     * @param num1
     * @param num2
     * @param point 请使用BigDecimalUtils.PRICE_DECIMAL_POINT | BigDecimalUtils.WEIGHT_DECIMAL_POINT
     * @return BigDecimal
     */
    fun add(num1: BigDecimal, num2: BigDecimal?, point: Int): BigDecimal {
        return setScale(num1.add(num2), point)
    }

    /**
     * 累加
     *
     * @param point
     * @param array
     * @return
     */
    fun add(point: Int, vararg array: BigDecimal?): BigDecimal {
        var res = BigDecimal(0)
        for (item in array) {
            res = if (item == null) {
                res.add(BigDecimal.ZERO)
            } else {
                res.add(item)
            }
        }
        return setScale(res, point)
    }

    /**
     * BigDecimal 乘法 num1 x num2
     * 未做非空校验
     *
     * @param num1
     * @param num2
     * @param point 请使用BigDecimalUtils.PRICE_DECIMAL_POINT | BigDecimalUtils.WEIGHT_DECIMAL_POINT
     * @return BigDecimal
     */
    fun multiply(num1: BigDecimal, num2: BigDecimal?, point: Int): BigDecimal {
        return setScale(num1.multiply(num2), point)
    }

    /**
     * BigDecimal 减法 num1 - num2
     * 未做非空校验
     *
     * @param num1
     * @param num2
     * @param point 请使用BigDecimalUtils.PRICE_DECIMAL_POINT | BigDecimalUtils.WEIGHT_DECIMAL_POINT
     * @return BigDecimal
     */
    fun subtract(num1: BigDecimal, num2: BigDecimal?, point: Int): BigDecimal {
        return setScale(num1.subtract(num2), point)
    }

    /**
     * BigDecimal 除法 num1/num2
     * 未做非空校验
     *
     * @param num1
     * @param num2
     * @param point 请使用BigDecimalUtils.PRICE_DECIMAL_POINT | BigDecimalUtils.WEIGHT_DECIMAL_POINT
     * @return BigDecimal
     */
    fun divide(num1: BigDecimal, num2: BigDecimal?, point: Int): BigDecimal {
        return num1.divide(num2, point, RoundingMode.HALF_UP)
    }

    /**
     * 设置小数点类型为 四舍五入
     *
     * @param num
     * @param point
     * @return BigDecimal
     */
    fun setScale(num: BigDecimal, point: Int): BigDecimal {
        return num.setScale(point, RoundingMode.HALF_UP)
    }

    /**
     * 比较 num1 是否大于 num2
     *
     * @param num1
     * @param num2
     * @return boolean
     */
    fun isGreaterThan(num1: BigDecimal, num2: BigDecimal?): Boolean {
        return num1.compareTo(num2) > 0
    }

    /**
     * 比较 num1 是否大于等于 num2
     *
     * @param num1
     * @param num2
     * @return boolean
     */
    fun isGreaterOrEqual(num1: BigDecimal, num2: BigDecimal?): Boolean {
        return isGreaterThan(num1, num2) || equals(num1, num2)
    }

    /**
     * 比较 num1 是否小于 num2
     *
     * @param num1
     * @param num2
     * @return boolean
     */
    fun isLessThan(num1: BigDecimal, num2: BigDecimal?): Boolean {
        return num1.compareTo(num2) < 0
    }

    /**
     * 比较 num1 是否小于等于 num2
     *
     * @param num1
     * @param num2
     * @return boolean
     */
    fun isLessOrEqual(num1: BigDecimal, num2: BigDecimal?): Boolean {
        return isLessThan(num1, num2) || equals(num1, num2)
    }

    /**
     * 比较 num1 是否等于 num2
     *
     * @param num1
     * @param num2
     * @return
     */
    fun equals(num1: BigDecimal, num2: BigDecimal?): Boolean {
        return num1.compareTo(num2) == 0
    }

    /**
     * 计算 num1 / num2 的百分比
     *
     * @param num1
     * @param num2
     * @param point 保留几位小数
     * @return String
     */
    fun percent(num1: Int?, num2: Int?, point: Int): BigDecimal {
        if (num1 == null || num2 == null) {
            return BigDecimal.ZERO
        }
        if (num2 == 0) {
            return BigDecimal.ZERO
        }
        return percent(BigDecimal(num1), BigDecimal(num2), point)
    }

    /**
     * 计算 num1 / num2 的百分比
     *
     * @param num1
     * @param num2
     * @param point 保留几位小数
     * @return String
     */
    fun percent(num1: BigDecimal?, num2: BigDecimal?, point: Int): BigDecimal {
        if (num1 == null || num2 == null) {
            return BigDecimal.ZERO
        }
        if (equals(BigDecimal.ZERO, num2)) {
            return BigDecimal.ZERO
        }
        val percent = num1.divide(num2, point + 2, RoundingMode.HALF_UP)
        return percent.multiply(ONE_HUNDRED).setScale(point)
    }

    /**
     * 比较 num1，num2 返回最大的值
     *
     * @param num1
     * @param num2
     * @return BigDecimal
     */
    fun max(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
        return if (num1.compareTo(num2) > 0) num1 else num2!!
    }

    /**
     * 比较 num1，num2 返回最小的值
     *
     * @param num1
     * @param num2
     * @return BigDecimal
     */
    fun min(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
        return if (num1.compareTo(num2) < 0) num1 else num2!!
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(percent(BigDecimal("3"), BigDecimal("11"), 2))
        println(percent(BigDecimal("8"), BigDecimal("11"), 2))
    }

    /**
     * 金额相关计算方法：四舍五入 保留2位小数点
     */
    object Amount {
        fun add(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.add(num2), AMOUNT_DECIMAL_POINT)
        }

        fun multiply(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.multiply(num2), AMOUNT_DECIMAL_POINT)
        }

        fun subtract(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.subtract(num2), AMOUNT_DECIMAL_POINT)
        }

        fun divide(num1: BigDecimal, num2: BigDecimal?): BigDecimal {
            return setScale(num1.divide(num2, RoundingMode.HALF_UP), AMOUNT_DECIMAL_POINT)
        }
    }
}
