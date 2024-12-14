package com.zyf.support.job.core

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.support.SmartJob
import com.zyf.support.job.config.SmartJobConfig
import com.zyf.support.job.constant.SmartJobTriggerTypeEnum
import com.zyf.support.job.constant.SmartJobUtil
import org.apache.commons.lang3.tuple.Pair
import org.springframework.scheduling.Trigger
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.CronTrigger
import org.springframework.scheduling.support.PeriodicTrigger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

/**
 * 定时任务 调度管理
 *
 * @author huke
 * @date 2024/6/22 21:30
 */
@Slf4j
object SmartJobScheduler {
    /**
     * Spring线程池任务调度器
     */
    private var TASK_SCHEDULER: ThreadPoolTaskScheduler? = null

    /**
     * 定时任务 map
     */
    private var JOB_FUTURE_MAP: MutableMap<String, Pair<SmartJob, ScheduledFuture<*>?>>? = null

    /**
     * 初始化任务调度配置
     */
    fun init(config: SmartJobConfig) {
        TASK_SCHEDULER = ThreadPoolTaskScheduler()
        val threadFactory = ThreadFactoryBuilder().setNameFormat("SmartJobExecutor-%d").build()
        TASK_SCHEDULER!!.setThreadFactory(threadFactory)
        TASK_SCHEDULER!!.poolSize = config.corePoolSize
        // 线程池在关闭时会等待所有任务完成
        TASK_SCHEDULER!!.setWaitForTasksToCompleteOnShutdown(true)
        // 在调用shutdown方法后，等待任务完成的最长时间
        TASK_SCHEDULER!!.setAwaitTerminationSeconds(10)
        // 错误处理
        TASK_SCHEDULER!!.setErrorHandler { t: Throwable? -> log.error("SmartJobExecute Err:", t) }
        // 当一个任务在被调度执行前被取消时，是否应该从线程池的任务队列中移除
        TASK_SCHEDULER!!.isRemoveOnCancelPolicy = true
        TASK_SCHEDULER!!.initialize()

        JOB_FUTURE_MAP = ConcurrentHashMap()
    }

    /**
     * 获取任务执行对象
     *
     * @param jobId
     * @return
     */
    fun getJobFuture(jobId: String): ScheduledFuture<*>? {
        val pair = JOB_FUTURE_MAP!![jobId] ?: return null
        return pair.right
    }

    val jobInfo: List<SmartJob>
        /**
         * 获取当前所有执行任务
         *
         * @return
         */
        get() = JOB_FUTURE_MAP!!.values.stream().map { obj: Pair<SmartJob, ScheduledFuture<*>?> -> obj.left }.collect(Collectors.toList())

    /**
     * 获取任务执行实体类
     *
     * @param jobId
     * @return
     */
    fun getJobInfo(jobId: String): SmartJob? {
        val pair = JOB_FUTURE_MAP!![jobId] ?: return null
        return pair.left
    }

    /**
     * 添加任务
     *
     * @param jobExecute
     * @return
     */
    fun addJob(jobExecute: SmartJobExecutor) {
        // 任务是否存在
        val jobEntity = jobExecute.job
        val jobId: String = jobEntity.jobId
        if (JOB_FUTURE_MAP!!.containsKey(jobId)) {
            // 移除任务
            removeJob(jobId)
        }
        // 任务触发类型
        var trigger: Trigger? = null
        val triggerType: String = jobEntity.triggerType
        val triggerValue: String = jobEntity.triggerValue
        // 优先 cron 表达式
        if (SmartJobTriggerTypeEnum.CRON.equalsValue(triggerType)) {
            trigger = CronTrigger(triggerValue)
        } else if (SmartJobTriggerTypeEnum.FIXED_DELAY.equalsValue(triggerType)) {
            trigger = PeriodicTrigger(SmartJobUtil.getFixedDelayVal(triggerValue), TimeUnit.SECONDS)
        }
        val jobName: String = jobEntity.jobName
        if (null == trigger) {
            log.error("==== SmartJob ==== trigger-value not null {}", jobName)
            return
        }
        // 执行任务
        val schedule = TASK_SCHEDULER!!.schedule(jobExecute, trigger)
        JOB_FUTURE_MAP!![jobId] = Pair.of(jobEntity, schedule)
        log.info("==== SmartJob ==== add job:{}", jobName)
    }

    /**
     * 移除任务
     * 等待任务执行完成后移除
     *
     * @param jobId
     */
    fun removeJob(jobId: String) {
        val jobFuture = getJobFuture(jobId) ?: return
        // 结束任务
        stopJob(jobFuture)
        JOB_FUTURE_MAP!!.remove(jobId)
        log.info("==== SmartJob ==== remove job:{}", jobId)
    }

    /**
     * 停止所有定时任务
     */
    fun destroy() {
        // 启动一个有序的关闭过程,在这个过程中,不再接受新的任务提交,但已提交的任务（包括正在执行的和队列中等待的）会被允许执行完成。
        TASK_SCHEDULER!!.destroy()
        JOB_FUTURE_MAP!!.clear()
    }

    /**
     * 结束任务
     * 如果任务还没有开始执行，会直接被取消。
     * 如果任务已经开始执行，此时不会中断执行中的线程，任务会执行完成再被取消
     *
     * @param scheduledFuture
     */
    private fun stopJob(scheduledFuture: ScheduledFuture<*>?) {
        if (null == scheduledFuture || scheduledFuture.isCancelled) {
            return
        }
        scheduledFuture.cancel(false)
    }
}
