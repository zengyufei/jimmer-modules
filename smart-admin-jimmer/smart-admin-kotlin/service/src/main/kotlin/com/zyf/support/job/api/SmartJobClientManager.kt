package com.zyf.support.job.api

import cn.hutool.core.util.IdUtil
import com.google.common.collect.Lists
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.repository.support.SmartJobLogRepository
import com.zyf.repository.support.SmartJobRepository
import com.zyf.support.SmartJob
import com.zyf.support.copy
import com.zyf.support.job.api.domain.SmartJobMsg
import com.zyf.support.job.config.SmartJobAutoConfiguration
import com.zyf.support.job.core.ISmartJob
import com.zyf.support.job.core.SmartJobExecutor
import com.zyf.support.job.core.SmartJobLauncher
import jakarta.annotation.PreDestroy
import org.redisson.api.RTopic
import org.redisson.api.RedissonClient
import org.redisson.api.listener.MessageListener
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Predicate

/**
 * smart job 执行端管理
 * 分布式系统之间 用发布/订阅消息的形式 来管理多个job
 *
 * @author huke
 * @date 2024/6/22 20:31
 */
@ConditionalOnBean(SmartJobAutoConfiguration::class)
@Slf4j
@Service
class SmartJobClientManager(
    val jobLauncher: SmartJobLauncher,
    val jobRepository: SmartJobRepository,
    val jobLogRepository: SmartJobLogRepository,
    val jobInterfaceList: List<ISmartJob>,
    private val redissonClient: RedissonClient
) {

    private val topic: RTopic

    private val jobMsgListener: SmartJobMsgListener

    init {
        // 添加监听器
        this.topic = redissonClient.getTopic(TOPIC)
        this.jobMsgListener = SmartJobMsgListener()
        topic.addListener(SmartJobMsg::class.java, jobMsgListener)
        log.info("==== SmartJob ==== client-manager init")
    }

    /**
     * 发布消息
     */
    fun publishToClient(msgDTO: SmartJobMsg) {
        msgDTO.msgId = IdUtil.fastSimpleUUID()
        topic.publish(msgDTO)
    }

    /**
     * 处理消息
     */
    private inner class SmartJobMsgListener : MessageListener<SmartJobMsg> {
        override fun onMessage(channel: CharSequence, msg: SmartJobMsg) {
            log.info("==== SmartJob ==== on-message :{}", msg)
            // 判断消息类型 业务简单就直接判断 复杂的话可以策略模式
            val msgType: SmartJobMsg.MsgTypeEnum = msg.msgType!!
            // 更新任务
            if (SmartJobMsg.MsgTypeEnum.UPDATE_JOB === msgType) {
                updateJob(msg.jobId!!)
            }
            // 执行任务
            if (SmartJobMsg.MsgTypeEnum.EXECUTE_JOB === msgType) {
                executeJob(msg)
            }
        }
    }

    /**
     * 获取任务执行类
     *
     * @param jobClass
     * @return
     */
    private fun queryJobImpl(jobClass: String): ISmartJob {
        return jobInterfaceList.first { it.className == jobClass }
    }

    /**
     * 更新任务
     *
     * @param jobId
     */
    private fun updateJob(jobId: String) {
        val jobEntity: SmartJob = jobRepository.byId(jobId) ?: return
        jobLauncher.startOrRefreshJob(Lists.newArrayList(jobEntity))
    }

    /**
     * 立即执行任务
     *
     * @param msg
     */
    private fun executeJob(msg: SmartJobMsg) {
        val jobId: String = msg.jobId!!
        val jobEntity: SmartJob = jobRepository.byId(jobId) ?: return
        // 获取定时任务实现类
        val optional= this.queryJobImpl(jobEntity.jobClass)

        // 获取执行锁 无需主动释放
        val rLock = redissonClient.getLock(EXECUTE_LOCK + msg.msgId)
        try {
            val getLock = rLock.tryLock(0, 20, TimeUnit.SECONDS)
            if (!getLock) {
                return
            }
        } catch (e: InterruptedException) {
            log.error("==== SmartJob ==== msg execute err:", e)
            return
        }

        // 通过执行器 执行任务
        val jobExecutor = SmartJobExecutor(jobEntity.copy {
            param = msg.param!!
        }, jobRepository, jobLogRepository, optional, redissonClient)
        jobExecutor.execute(msg.updateName!!)
    }


    @PreDestroy
    fun destroy() {
        topic.removeListener(jobMsgListener)
    }


    companion object {
        private const val EXECUTE_LOCK = "smart-job-lock-msg-execute-"

        private const val TOPIC = "smart-job-instance"
    }
}
