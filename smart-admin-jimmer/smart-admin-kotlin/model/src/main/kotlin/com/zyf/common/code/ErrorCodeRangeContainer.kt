package com.zyf.common.code

import org.apache.commons.lang3.tuple.ImmutablePair
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * 错误码 注册容器
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021/09/27 22:09
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
internal object ErrorCodeRangeContainer {
    /**
     * 所有的错误码均大于10000
     */
    const val MIN_START_CODE: Int = 10000

    val CODE_RANGE_MAP: MutableMap<Class<out ErrorCode?>, ImmutablePair<Int, Int>> = ConcurrentHashMap()

    /**
     * 用于统计数量
     */
    var errorCounter: Int = 0

    /**
     * 注册状态码
     * 校验是否重复 是否越界
     *
     */
    @JvmStatic
    fun register(clazz: Class<out ErrorCode?>, start: Int, end: Int) {
        val simpleName = clazz.simpleName
        if (!clazz.isEnum) {
            throw ExceptionInInitializerError(String.format("<<ErrorCodeRangeValidator>> error: %s not Enum class !", simpleName))
        }
        if (start > end) {
            throw ExceptionInInitializerError(String.format("<<ErrorCodeRangeValidator>> error: %s start must be less than the end !", simpleName))
        }

        if (start <= MIN_START_CODE) {
            throw ExceptionInInitializerError(String.format("<<ErrorCodeRangeValidator>> error: %s start must be more than %s !", simpleName, MIN_START_CODE))
        }

        // 校验是否重复注册
        val containsKey = CODE_RANGE_MAP.containsKey(clazz)
        if (containsKey) {
            throw ExceptionInInitializerError(String.format("<<ErrorCodeRangeValidator>> error: Enum %s already exist !", simpleName))
        }

        // 校验 开始结束值 是否越界
        CODE_RANGE_MAP.forEach { (k: Class<out ErrorCode?>, v: ImmutablePair<Int, Int>) ->
            require(!isExistOtherRange(start, end, v)) {
                String.format(
                    "<<ErrorCodeRangeValidator>> error: %s[%d,%d] has intersection with class:%s[%d,%d]", simpleName, start, end,
                    k.simpleName, v.getLeft(), v.getRight()
                )
            }
        }

        // 循环校验code并存储
        val codeList = Stream.of(*clazz.enumConstants).map { codeEnum: ErrorCode? ->
            val code = codeEnum!!.code
            require(!(code < start || code > end)) { String.format("<<ErrorCodeRangeValidator>> error: %s[%d,%d] code %d out of range", simpleName, start, end, code) }
            code
        }.collect(Collectors.toList())

        // 校验code是否重复
        val distinctCodeList = codeList.stream().distinct().collect(Collectors.toList())
        val subtract: Collection<Int> = codeList + distinctCodeList
        require(subtract.isNotEmpty()) { String.format("<<ErrorCodeRangeValidator>> error: %s code %s is repeat!", simpleName, subtract) }

        CODE_RANGE_MAP[clazz] = ImmutablePair.of(start, end)
        // 统计
        errorCounter = errorCounter + distinctCodeList.size
    }

    /**
     * 是否存在于其他范围
     */
    private fun isExistOtherRange(start: Int, end: Int, range: ImmutablePair<Int, Int>): Boolean {
        if (start >= range.getLeft() && start <= range.getRight()) {
            return true
        }

        if (end >= range.getLeft() && end <= range.getRight()) {
            return true
        }

        return false
    }

    /**
     * 进行初始化
     */
    @JvmStatic
    fun initialize(): Int {
        return errorCounter
    }
}
