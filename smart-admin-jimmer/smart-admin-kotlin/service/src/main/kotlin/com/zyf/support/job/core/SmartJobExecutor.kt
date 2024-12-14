package com.zyf.support.job.core

import cn.hutool.core.exceptions.ExceptionUtil
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.utils.SmartIpUtil
import com.zyf.repository.support.SmartJobLogRepository
import com.zyf.repository.support.SmartJobRepository
import com.zyf.support.SmartJob
import com.zyf.support.SmartJobLog
import com.zyf.support.job.constant.SmartJobConst
import com.zyf.support.job.constant.SmartJobUtil
import jakarta.annotation.Resource
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StopWatch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

/**
 * 定时任务 执行器
 *
 * @author huke
 * @date 2024/6/17 21:30
 */
@Slf4j
class SmartJobExecutor(
    private val jobEntity: SmartJob,
    private val smartJobRepository: SmartJobRepository,
    private val smartJobLogRepository: SmartJobLogRepository,
    private val jobInterface: ISmartJob,
    private val redissonClient: RedissonClient
) : Runnable {
    /**
     * 系统线程执行
     */
    override fun run() {
        // 获取当前任务执行锁 最多持有30s自动释放
        val jobId = jobEntity.jobId
        val rLock = redissonClient.getLock(EXECUTE_LOCK + jobId)
        try {
            val lock = rLock.tryLock(0, 30, TimeUnit.SECONDS)
            if (!lock) {
                return
            }
            // 查询上次执行时间 校验执行间隔
            val dbJobEntity: SmartJob = smartJobRepository.byId(jobId) ?: return
            val lastExecuteTime: LocalDateTime? = dbJobEntity.lastExecuteTime
            val nextTime = SmartJobUtil.queryNextTimeFromLast(jobEntity.triggerType, jobEntity.triggerValue, lastExecuteTime, 1)[0]
            if (LocalDateTime.now().isBefore(nextTime)) {
                return
            }
            // 执行任务
            val logEntity: SmartJobLog = this.execute(SmartJobConst.SYSTEM_NAME)
            log.info("==== SmartJob ==== execute job->{},time-millis->{}ms", jobEntity.jobName, logEntity.executeTimeMillis)
        } catch (t: Throwable) {
            log.error("==== SmartJob ==== execute err:", t)
        } finally {
            if (rLock.isHeldByCurrentThread) {
                rLock.unlock()
            }
        }
    }

    /**
     * 执行任务
     *
     * @param executorName
     */
    fun execute(executorName: String): SmartJobLog {
        // 保存执行记录
        val startTime = LocalDateTime.now()
        val inputLogId = this.saveLogBeforeExecute(jobEntity, executorName, startTime)

        // 执行计时
        val stopWatch = StopWatch()
        stopWatch.start()

        // 执行任务
        var inputSuccessFlag = true
        var inputExecuteResult: String?
        try {
            inputExecuteResult = jobInterface.run(jobEntity.param)
            stopWatch.stop()
        } catch (t: Throwable) {
            stopWatch.stop()
            inputSuccessFlag = false
            // ps:异常信息不大于数据库字段长度限制
            inputExecuteResult = ExceptionUtil.stacktraceToString(t, 1800)
            log.error("==== SmartJob ==== execute err:", t)
        }

        // 更新执行记录
        val logEntity = SmartJobLog {
            logId = inputLogId
            successFlag = inputSuccessFlag
            executeTimeMillis = stopWatch.totalTimeMillis
            executeEndTime = startTime.plus(stopWatch.totalTimeMillis, ChronoUnit.MILLIS)
            executeResult = inputExecuteResult!!
        }
        smartJobLogRepository.update(logEntity)
        return logEntity
    }

    /**
     * 执行前 保存执行记录
     *
     * @param jobEntity
     * @param executorName
     * @param executeTime
     * @return 返回执行记录id
     */
    private fun saveLogBeforeExecute(
        jobEntity: SmartJob,
        executorName: String,
        executeTime: LocalDateTime
    ): String {
        val inputJobId: String = jobEntity.jobId
        // 保存执行记录
        val logEntity = SmartJobLog {
            jobId=inputJobId
            jobName=jobEntity.jobName
            param=jobEntity.param
            successFlag=true
            executeStartTime=executeTime
            executeEndTime=executeTime
            executeTimeMillis=0L
            createName=executorName
            SmartIpUtil.localFirstIp?.let{ip=it}
            processId=SmartJobUtil.processId
            programPath=SmartJobUtil.programPath
        }


        // 更新最后执行时间
        val updateJobEntity = SmartJob {
            jobId = inputJobId
            lastExecuteTime = executeTime
        }
        val logId = smartJobRepository.saveLog(logEntity, updateJobEntity)
        return logId
    }

    val job: SmartJob
        /**
         * 查询 当前任务信息
         *
         * @return
         */
        get() = jobEntity

    companion object {
        private const val EXECUTE_LOCK = "smart-job-lock-execute-"
    }
}
