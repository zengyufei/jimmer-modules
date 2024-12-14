package com.zyf.support.job.api.domain

import java.time.LocalDateTime

/**
 * 定时任务-执行记录 vo
 *
 * @author huke
 * @date 2024/6/17 21:30
 */
class SmartJobLogDetailVO {
    /** logId  */
    var logId: Long? = null

    /** 任务id  */
    var jobId: String? = null

    /** 任务名称  */
    var jobName: String? = null

    /** 定时任务参数|可选  */
    var param: String? = null

    /** 执行结果是否成功  */
    var successFlag: Boolean? = null

    /** 开始执行时间  */
    var executeStartTime: LocalDateTime? = null

    /** 执行时长-毫秒  */
    var executeTimeMillis: Long? = null

    /** 执行结果描述  */
    var executeResult: String? = null

    /** 执行结束时间  */
    var executeEndTime: LocalDateTime? = null

    /** ip  */
    var ip: String? = null

    /** 进程id  */
    var processId: String? = null

    /** 程序目录  */
    var programPath: String? = null

    var createName: String? = null

    var createTime: LocalDateTime? = null
}
