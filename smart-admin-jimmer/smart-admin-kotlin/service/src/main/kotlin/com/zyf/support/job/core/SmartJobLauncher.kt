package com.zyf.support.job.core

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.repository.support.SmartJobLogRepository
import com.zyf.repository.support.SmartJobRepository
import com.zyf.support.SmartJob
import com.zyf.support.job.config.SmartJobConfig
import com.zyf.support.job.constant.SmartJobConst
import com.zyf.support.job.constant.SmartJobUtil
import jakarta.annotation.PreDestroy
import org.redisson.api.RedissonClient
import org.springframework.util.CollectionUtils
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.function.Function
import java.util.stream.Collectors

/**
 * 定时任务 作业启动类
 *
 * @author huke
 * @date 2024/6/17 21:30
 */
@Slf4j
class SmartJobLauncher(
    private val jobConfig: SmartJobConfig,
    private val jobRepository: SmartJobRepository,
    private val jobLogRepository: SmartJobLogRepository,
    private val jobInterfaceList: List<ISmartJob?>,
    private val redissonClient: RedissonClient
) {

    init {
        // init job scheduler
        SmartJobScheduler.init(jobConfig)

        // 任务自动检测配置 固定1个线程
        val initDelay: Int = jobConfig.initDelay
        val refreshEnabled: Boolean = jobConfig.dbRefreshEnabled
        val refreshInterval: Int = jobConfig.dbRefreshInterval

        val factory = ThreadFactoryBuilder().setNameFormat("SmartJobLauncher-%d").build()
        val executor = ScheduledThreadPoolExecutor(1, factory)
        val launcherRunnable = Runnable {
            try {
                // 查询所有任务
                val smartJobList = this.queryJob()
                this.startOrRefreshJob(smartJobList)
            } catch (t: Throwable) {
                log.error("SmartJob Error:", t)
            }
            // 只在启动时 执行一次
            if (!refreshEnabled) {
                executor.shutdown()
            }
        }
        executor.scheduleWithFixedDelay(launcherRunnable, initDelay.toLong(), refreshInterval.toLong(), TimeUnit.SECONDS)

        // 打印信息
        val refreshDesc = if (refreshEnabled) "开启|检测间隔" + refreshInterval + "秒" else "关闭"
        val format = String.format(SmartJobConst.LOGO, jobConfig.corePoolSize, initDelay, refreshDesc)
        SmartJobUtil.printInfo(format)
    }

    /**
     * 查询数据库
     * 启动/刷新任务
     */
    fun startOrRefreshJob(smartJobList: List<SmartJob>) {
        // 查询任务配置
        if (CollectionUtils.isEmpty(smartJobList) || CollectionUtils.isEmpty(jobInterfaceList)) {
            log.info("==== SmartJob ==== job list empty")
            return
        }

        // 任务实现类
        val jobImplMap = jobInterfaceList.stream().collect(Collectors.toMap(Function { obj: ISmartJob? -> obj!!.className }, Function.identity()))
        for (jobEntity in smartJobList) {
            // 任务是否存在 判断是否需要更新
            val jobId: String = jobEntity.jobId
            val oldJobEntity = SmartJobScheduler.getJobInfo(jobId)
            if (null != oldJobEntity) {
                // 不需要更新
                if (!isNeedUpdate(oldJobEntity, jobEntity)) {
                    continue
                }
                // 需要更新 移除原任务
                SmartJobScheduler.removeJob(jobId)
            }
            // 任务未开启
            if (!jobEntity.enabledFlag) {
                continue
            }
            // 查找任务实现类
            val jobImpl = jobImplMap[jobEntity.jobClass] ?: continue
            // 添加任务
            val jobExecute = SmartJobExecutor(jobEntity, jobRepository, jobLogRepository, jobImpl, redissonClient)
            SmartJobScheduler.addJob(jobExecute)
        }
        val runjJobList = SmartJobScheduler.jobInfo
        val jobNameList = runjJobList.stream().map<Any>(SmartJob::jobName).collect(Collectors.toList())
        log.info("==== SmartJob ==== start/refresh job num:{}->{}", runjJobList.size, jobNameList)
    }

    /**
     * 查询全部任务
     *
     * @return
     */
    private fun queryJob(): List<SmartJob> {
        return jobRepository.listAll()
    }

    @PreDestroy
    fun destroy() {
        SmartJobScheduler.destroy()
        log.info("==== SmartJob ==== destroy job")
    }

    companion object {
        /**
         * 手动判断 任务配置 是否需要更新
         * 新增字段的话 在这个方法里增加判断
         *
         * @return
         */
        private fun isNeedUpdate(oldJob: SmartJob, newJob: SmartJob): Boolean {
            // cron为空时 fixedDelay 才有意义
            return (oldJob.enabledFlag != newJob.enabledFlag
                    || oldJob.triggerType != newJob.triggerType
                    || oldJob.triggerValue != newJob.triggerValue
                    || oldJob.jobClass != newJob.jobClass)
        }
    }
}
