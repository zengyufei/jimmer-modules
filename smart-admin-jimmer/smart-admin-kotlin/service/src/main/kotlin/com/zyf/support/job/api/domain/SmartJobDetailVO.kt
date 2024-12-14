package com.zyf.support.job.api.domain

import com.zyf.cfg.json.serializer.enumeration.EnumSerialize
import com.zyf.support.job.constant.SmartJobTriggerTypeEnum
import java.time.LocalDateTime

/**
 * 定时任务 vo
 *
 * @author huke
 * @date 2024/6/17 21:30
 */
class SmartJobDetailVO {
    /** 任务id  */
    var jobId: String? = null

    /** 任务名称  */
    var jobName: String? = null

    /** 执行类  */
    var jobClass: String? = null

    @EnumSerialize(SmartJobTriggerTypeEnum::class)
    var triggerType: String? = null

    /** 触发配置  */
    var triggerValue: String? = null

    /** 定时任务参数|可选  */
    var param: String? = null

    /** 是否启用  */
    var enabledFlag: Boolean? = null

    /** 最后一执行时间  */
    var lastExecuteTime: LocalDateTime? = null

    /** 最后一次执行记录id  */
    var lastExecuteLogId: String? = null

    /** 备注  */
    var remark: String? = null

    /** 排序  */
    var sort: Int? = null

    var updateName: String? = null

    var updateTime: LocalDateTime? = null

    var createTime: LocalDateTime? = null

    /** 上次执行记录  */
    var lastJobLog: SmartJobLogDetailVO? = null

    /** 未来N次任务执行时间  */
    var nextJobExecuteTimeList: List<LocalDateTime>? = null
}
