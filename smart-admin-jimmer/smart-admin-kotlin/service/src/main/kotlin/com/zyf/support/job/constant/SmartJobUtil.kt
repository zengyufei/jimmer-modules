package com.zyf.support.job.constant

import org.springframework.scheduling.support.CronExpression
import java.lang.management.ManagementFactory
import java.time.LocalDateTime

/**
 * smart job util
 *
 * @author huke
 * @date 2024/6/18 20:00
 */
object SmartJobUtil {
    /**
     * 校验cron表达式 是否合法
     *
     * @param cron
     * @return
     */
    fun checkCron(cron: String?): Boolean {
        return CronExpression.isValidExpression(cron)
    }

    /**
     * 校验固定间隔 值是否合法
     *
     * @param val
     * @return
     */
    fun checkFixedDelay(`val`: String): Boolean {
        val intVal: Int
        try {
            intVal = `val`.toInt()
        } catch (e: NumberFormatException) {
            return false
        }
        return intVal > 0
    }

    /**
     * 打印一些展示信息到控制台
     * 环保绿
     *
     * @param info
     */
    fun printInfo(info: String?) {
        System.out.printf("\u001b[32;1m %s \u001b[0m", info)
    }

    /**
     * 查询未来N次执行时间 从最后一次时间时间 开始计算
     *
     * @param triggerType
     * @param triggerVal
     * @param lastExecuteTime
     * @param num
     * @return
     */
    fun queryNextTimeFromLast(
        triggerType: String?,
        triggerVal: String,
        lastExecuteTime: LocalDateTime?,
        num: Int
    ): List<LocalDateTime> {
        var nextTimeList: List<LocalDateTime>? = null
        if (SmartJobTriggerTypeEnum.CRON.equalsValue(triggerType!!)) {
            nextTimeList = queryNextTime(triggerVal, lastExecuteTime, num)
        } else if (SmartJobTriggerTypeEnum.FIXED_DELAY.equalsValue(triggerType)) {
            nextTimeList = queryNextTime(getFixedDelayVal(triggerVal), lastExecuteTime, num)
        }
        return nextTimeList!!
    }

    /**
     * 查询未来N次执行时间 从当前时间 开始计算
     *
     * @param triggerType
     * @param triggerVal
     * @param lastExecuteTime
     * @param num
     * @return
     */
    fun queryNextTimeFromNow(
        triggerType: String?,
        triggerVal: String?,
        lastExecuteTime: LocalDateTime?,
        num: Int
    ): List<LocalDateTime> {
        val nowTime = LocalDateTime.now()
        var nextTimeList: List<LocalDateTime> = mutableListOf()
        if (SmartJobTriggerTypeEnum.CRON.equalsValue(triggerType!!)) {
            nextTimeList = queryNextTime(triggerVal, nowTime, num)
        } else if (SmartJobTriggerTypeEnum.FIXED_DELAY.equalsValue(triggerType)) {
            val fixedDelay = getFixedDelayVal(triggerVal!!)
            val startTime = if (null == lastExecuteTime || lastExecuteTime.plusSeconds(fixedDelay.toLong()).isBefore(nowTime)
            ) nowTime else lastExecuteTime
            nextTimeList = queryNextTime(fixedDelay, startTime, num)
        }
        return nextTimeList
    }

    /**
     * 根据cron表达式 计算N次执行时间
     *
     * @param cron
     * @param startTime
     * @param num
     * @return
     */
    fun queryNextTime(cron: String?, startTime: LocalDateTime?, num: Int): List<LocalDateTime> {
        var startTime = startTime ?: return emptyList<LocalDateTime>()
        val parse = CronExpression.parse(cron!!)
        val timeList: MutableList<LocalDateTime> = ArrayList(num)
        for (i in 0 until num) {
            startTime = parse.next(startTime)!!
            timeList.add(startTime)
        }
        return timeList
    }

    /**
     * 根据 固定间隔 计算N次执行时间
     *
     * @param fixDelaySecond
     * @param startTime
     * @param num
     * @return
     */
    fun queryNextTime(fixDelaySecond: Long, startTime: LocalDateTime?, num: Int): List<LocalDateTime> {
        var startTime = startTime ?: return emptyList()
        val timeList: MutableList<LocalDateTime> = ArrayList(num)
        for (i in 0 until num) {
            startTime = startTime.plusSeconds(fixDelaySecond)
            timeList.add(startTime)
        }
        return timeList
    }

    /**
     * 获取固定间隔时间
     *
     * @param val
     * @return
     */
    fun getFixedDelayVal(`val`: String): Long {
        return `val`.toLong()
    }

    val programPath: String
        /**
         * 获取当前 Java 应用程序的工作目录
         *
         * @return
         */
        get() = System.getProperty("user.dir")

    val processId: String
        /**
         * 获取当前 Java 应用程序的进程id
         *
         * @return
         */
        get() {
            val runtime = ManagementFactory.getRuntimeMXBean()
            return runtime.name.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        }

    @JvmStatic
    fun main(args: Array<String>) {
        val startTime = LocalDateTime.now()
        var timeList = queryNextTime("5 * * * * *", startTime, 3)
        println(timeList)

        timeList = queryNextTime(10, startTime, 3)
        println(timeList)

        println("project path ->" + programPath)
        println("project process id ->" + processId)
    }
}
