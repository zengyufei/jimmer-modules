package com.zyf.support

import com.zyf.common.annotations.Body
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.SwaggerTagConst
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.utils.SmartRequestUtil
import com.zyf.support.job.api.SmartJobService
import com.zyf.support.job.api.domain.SmartJobDetailVO
import com.zyf.support.job.config.SmartJobAutoConfiguration
import com.zyf.support.repeatsubmit.annoation.RepeatSubmit
import com.zyf.support.service.dto.*
import jakarta.validation.Valid
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.*

/**
 * 定时任务 管理接口
 *
 * @author huke
 * @date 2024/6/17 20:41
 */
@Tag(name = SwaggerTagConst.Support.JOB)
@RestController
@ConditionalOnBean(SmartJobAutoConfiguration::class)
class SmartJobController(
    val jobService: SmartJobService
) {
    @Operation(summary = "定时任务-立即执行 @huke")
    @PostMapping("/support/job/execute")
    @RepeatSubmit
    fun execute(@RequestBody executeForm: @Valid SmartJobExecuteForm): ResponseDTO<String?> {
        val requestUser = SmartRequestUtil.requestUser
        return jobService.execute(executeForm.copy(
            updateName = requestUser?.userName?:"admin"
        ))
    }

    @Operation(summary = "定时任务-查询详情 @huke")
    @GetMapping("/support/job/{jobId}")
    fun queryJobInfo(@PathVariable jobId: String): ResponseDTO<SmartJobDetailVO?> {
        return jobService.queryJobInfo(jobId)
    }

    @Operation(summary = "定时任务-分页查询 @huke")
    @PostMapping("/support/job/query")
    fun queryJob(
        @Body pageBean: PageBean,
        @RequestBody queryForm: @Valid SmartJobQueryForm): ResponseDTO<PageResult<SmartJobVO>> {
        return jobService.queryJob(pageBean, queryForm)
    }

    @Operation(summary = "定时任务-更新-任务信息 @huke")
    @PostMapping("/support/job/update")
    @RepeatSubmit
    fun updateJob(@RequestBody updateForm: @Valid SmartJobUpdateForm): ResponseDTO<String?> {
        val requestUser = SmartRequestUtil.requestUser
        return jobService.updateJob(updateForm.copy(
            updateName = requestUser?.userName?:"admin"
        ))
    }

    @Operation(summary = "定时任务-更新-开启状态 @huke")
    @PostMapping("/support/job/update/enabled")
    @RepeatSubmit
    fun updateJobEnabled(@RequestBody updateForm: @Valid SmartJobEnabledUpdateForm): ResponseDTO<String?> {
        val requestUser = SmartRequestUtil.requestUser
        return jobService.updateJobEnabled(updateForm.copy(
            updateName = requestUser?.userName?:"admin"
        ))
    }

    @Operation(summary = "定时任务-执行记录-分页查询 @huke")
    @PostMapping("/support/job/log/query")
    fun queryJobLog(
        @Body pageBean: PageBean,
        @RequestBody queryForm: @Valid SmartJobLogQueryForm
    ): ResponseDTO<PageResult<SmartJobLogVO>> {
        return jobService.queryJobLog(pageBean, queryForm)
    }
}