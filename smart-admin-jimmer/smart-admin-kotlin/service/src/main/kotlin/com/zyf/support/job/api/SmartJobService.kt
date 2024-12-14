package com.zyf.support.job.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.Lists
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.common.utils.SmartBeanUtil
import com.zyf.repository.support.SmartJobLogRepository
import com.zyf.repository.support.SmartJobRepository
import com.zyf.support.*
import com.zyf.support.job.api.domain.*
import com.zyf.support.job.api.domain.SmartJobDetailVO
import com.zyf.support.job.api.domain.SmartJobLogDetailVO
import com.zyf.support.job.config.SmartJobAutoConfiguration
import com.zyf.support.job.constant.SmartJobTriggerTypeEnum
import com.zyf.support.job.constant.SmartJobUtil
import com.zyf.support.service.dto.*
import org.apache.commons.collections4.CollectionUtils
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

/**
 * 定时任务 接口业务管理
 * 如果不需要通过接口管理定时任务 可以删除此类
 *
 * @author huke
 * @date 2024/6/17 20:41
 */
@ConditionalOnBean(SmartJobAutoConfiguration::class)
@Service
class SmartJobService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    private val smartJobRepository: SmartJobRepository,
    private val smartJobLogRepository: SmartJobLogRepository,
    private val jobClientManager: SmartJobClientManager
) {

    /**
     * 查询 定时任务详情
     *
     * @param jobId
     * @return
     */
    fun queryJobInfo(jobId: String?): ResponseDTO<SmartJobDetailVO?> {
        if (jobId == null) {
            return ResponseDTO.ok()
        }
        val jobEntity: SmartJob = smartJobRepository.byId(jobId) ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)
        val jobVO: SmartJobDetailVO = SmartBeanUtil.copy(jobEntity, SmartJobDetailVO::class.java)!!
        // 处理设置job详情
        this.handleJobInfo(Lists.newArrayList(jobVO))
        return ResponseDTO.ok(jobVO)
    }

    /**
     * 分页查询 定时任务
     *
     * @param queryForm
     * @return
     */
    fun queryJob(
        pageBean: PageBean,
        queryForm: SmartJobQueryForm): ResponseDTO<PageResult<SmartJobVO>> {
        val pageResult = sql.createQuery(SmartJob::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.sort.asc(), table.jobId.desc())

            where(queryForm)
            select(table.fetch(SmartJobVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 处理设置 任务信息
     *
     * @param jobList
     */
    private fun handleJobInfo(jobList: List<SmartJobDetailVO>) {
        if (CollectionUtils.isEmpty(jobList)) {
            return
        }
        // 查询最后一次执行记录
        val logIdList = jobList.mapNotNull { it.lastExecuteLogId }
        var lastLogMap: Map<String, SmartJobLogDetailVO?> = mutableMapOf()
        if (CollectionUtils.isNotEmpty(logIdList)) {
            val findByIds = smartJobRepository.findByIds(logIdList)
            lastLogMap = findByIds
                .associate { it.jobId to SmartBeanUtil.copy(it, SmartJobLogDetailVO::class.java) }
        }

        // 循环处理任务信息
        for (jobVO in jobList) {
            // 设置最后一次执行记录
            val lastExecuteLogId: String? = jobVO.lastExecuteLogId
            jobVO.lastJobLog = lastLogMap[lastExecuteLogId]
            // 计算未来5次执行时间
            if (jobVO.enabledFlag!!) {
                val nextTimeList: List<LocalDateTime> = SmartJobUtil.queryNextTimeFromNow(jobVO.triggerType, jobVO.triggerValue, jobVO.lastExecuteTime, 5)
                jobVO.nextJobExecuteTimeList = nextTimeList
            }
        }
    }

    /**
     * 分页查询 定时任务-执行记录
     *
     * @param queryForm
     * @return
     */
    fun queryJobLog(
        pageBean: PageBean,
        queryForm: SmartJobLogQueryForm
    ): ResponseDTO<PageResult<SmartJobLogVO>> {
        val pageResult = sql.createQuery(SmartJobLog::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.logId.desc())

            where(queryForm)
            select(table.fetch(SmartJobLogVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 更新定时任务
     *
     * @param updateForm
     * @return
     */
    fun updateJob(updateForm: SmartJobUpdateForm): ResponseDTO<String?> {
        // 校验参数
        val jobId: String = updateForm.jobId!!
        var jobEntity: SmartJob = smartJobRepository.byId(jobId) ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)
        // 校验触发时间配置
        val triggerType: String = updateForm.triggerType
        val triggerValue: String = updateForm.triggerValue!!
        if (SmartJobTriggerTypeEnum.CRON.equalsValue(triggerType) && !SmartJobUtil.checkCron(triggerValue)) {
            return ResponseDTO.userErrorParam("cron表达式错误")
        }
        if (SmartJobTriggerTypeEnum.FIXED_DELAY.equalsValue(triggerType) && !SmartJobUtil.checkFixedDelay(triggerValue)) {
            return ResponseDTO.userErrorParam("固定间隔错误：整数且大于0")
        }

        // 更新数据
        smartJobRepository.update(updateForm)

        // 更新执行端
        val jobMsg = SmartJobMsg()
        jobMsg.jobId = jobId
        jobMsg.msgType = SmartJobMsg.MsgTypeEnum.UPDATE_JOB
        jobMsg.updateName = updateForm.updateName
        jobClientManager.publishToClient(jobMsg)
        return ResponseDTO.ok()
    }

    /**
     * 更新定时任务-是否开启
     *
     * @param updateForm
     * @return
     */
    fun updateJobEnabled(updateForm: SmartJobEnabledUpdateForm): ResponseDTO<String?> {
        val inputJobId: String = updateForm.jobId!!
        var jobEntity: SmartJob? = smartJobRepository.byId(inputJobId) ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)
        val inputEnabledFlag: Boolean = updateForm.enabledFlag
        if (inputEnabledFlag == jobEntity!!.enabledFlag) {
            return ResponseDTO.ok()
        }
        // 更新数据
        jobEntity = SmartJob {
            jobId = inputJobId
            enabledFlag = inputEnabledFlag
            updateName = updateForm.updateName
        }
        sql.update(jobEntity)

        // 更新执行端
        val jobMsg = SmartJobMsg()
        jobMsg.jobId = inputJobId
        jobMsg.msgType = SmartJobMsg.MsgTypeEnum.UPDATE_JOB
        jobMsg.updateName = updateForm.updateName
        jobClientManager.publishToClient(jobMsg)

        return ResponseDTO.ok()
    }

    /**
     * 执行定时任务
     * 忽略任务的开启状态,立即执行一次
     *
     * @param executeForm
     * @return
     */
    fun execute(executeForm: SmartJobExecuteForm): ResponseDTO<String?> {
        val inputJobId: String = executeForm.jobId
        smartJobRepository.byId(inputJobId) ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)

        // 更新执行端
        val jobMsg = SmartJobMsg()
        jobMsg.jobId = inputJobId
        jobMsg.param = executeForm.param
        jobMsg.msgType = SmartJobMsg.MsgTypeEnum.EXECUTE_JOB
        jobMsg.updateName = executeForm.updateName

        jobClientManager.publishToClient(jobMsg)
        return ResponseDTO.ok()
    }

    /**
     * 新增定时任务
     * ps:目前没有业务场景需要通过接口 添加任务
     * 因为新增定时任务无论如何都需要 手动编码
     * 需要时手动给数据库增加一条就行
     *
     * @return
     * @author huke
     */
    fun addJob(): ResponseDTO<String?> {
        return ResponseDTO.userErrorParam("暂未支持")
    }

    /**
     * 移除定时任务
     * ps：目前没有业务场景需要通过接口移除，理由同 [SmartJobService.addJob]，
     * 彻底移除始终都需要手动删除代码
     * 如果只是想暂停任务执行，可以调用 [SmartJobService.updateJobEnabled]
     *
     * @return
     * @author huke
     */
    fun delJob(): ResponseDTO<String?> {
        return ResponseDTO.userErrorParam("暂未支持")
    }
}
