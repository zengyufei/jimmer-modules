package com.zyf.support.heartbeat.core

import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 心跳核心调度管理器
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-01-09 20:57:24
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class HeartBeatManager(
    /**
     * 调度配置信息
     */
    private val intervalMilliseconds: Long,
    /**
     * 服务状态持久化处理类
     */
    private val heartBeatRecordHandler: IHeartBeatRecordHandler
) {
    // 使用守护线程去处理
    private val threadPoolExecutor: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(THREAD_COUNT) { r: Runnable? ->
        val t = Thread(r, THREAD_NAME_PREFIX)
        if (!t.isDaemon) {
            t.isDaemon = true
        }
        t
    }

    /**
     */
    init {
        // 开始心跳
        this.beginHeartBeat()
    }

    /**
     * 开启心跳
     */
    private fun beginHeartBeat() {
        val heartBeatRunnable = HeartBeatRunnable(heartBeatRecordHandler)
        // INITIAL_DELAY = 首次执行前等待时间
        // intervalMilliseconds = 每个多少时间执行一次
        threadPoolExecutor.scheduleWithFixedDelay(heartBeatRunnable, INITIAL_DELAY, intervalMilliseconds, TimeUnit.MILLISECONDS)
    }

    companion object {
        private const val THREAD_NAME_PREFIX = "smart-heart-beat"
        private const val THREAD_COUNT = 1
        private const val INITIAL_DELAY = 60 * 1000L
    }
}
