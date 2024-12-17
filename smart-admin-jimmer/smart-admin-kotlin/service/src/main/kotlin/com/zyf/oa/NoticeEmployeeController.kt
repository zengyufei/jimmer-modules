package com.zyf.oa

import cn.hutool.extra.servlet.JakartaServletUtil
import com.zyf.common.annotations.Body
import com.zyf.common.annotations.OperateLog
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.AdminSwaggerTagConst
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.utils.SmartRequestUtil
import com.zyf.login.domain.RequestEmployee
import com.zyf.oa.service.NoticeEmployeeService
import com.zyf.oa.service.NoticeService
import com.zyf.service.dto.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*


/**
 * 公告、通知、新闻等等
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-08-12 21:40:39
 * @Wechat 卓大1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@RestController
@OperateLog
@Tag(name = AdminSwaggerTagConst.Business.OA_NOTICE)
class NoticeEmployeeController(
    private val noticeService: NoticeService,
    private val noticeEmployeeService: NoticeEmployeeService,
) {

    // --------------------- 【员工】查看 通知公告 -------------------------
    /** 【员工】通知公告-查看详情 @author 卓大 */
    @Operation(summary = "【员工】通知公告-查看详情 @author 卓大")
    @GetMapping("/oa/notice/employee/view/{noticeId}")
    fun view(@PathVariable noticeId: String, request: HttpServletRequest): ResponseDTO<NoticeDetailVO?> {

        val requestUser = SmartRequestUtil.requestUser!!
        return noticeEmployeeService.view(
            requestUser.userId!!,
            noticeId,
            JakartaServletUtil.getClientIP(request),
            request.getHeader("User-Agent")
        )
    }

    /** 【员工】通知公告-查询全部 @author 卓大 */
    @Operation(summary = "【员工】通知公告-查询全部 @author 卓大")
    @PostMapping("/oa/notice/employee/query")
    fun queryEmployeeNotice(
        @Body pageBean: PageBean,
        @RequestBody noticeEmployeeQueryForm: @Valid NoticeEmployeeQueryForm
    ): ResponseDTO<PageResult<NoticeEmployeeVO>> {

        val requestUser = SmartRequestUtil.requestUser!!
        return noticeEmployeeService.queryList(requestUser.userId!!, pageBean, noticeEmployeeQueryForm)
    }

    /** 【员工】通知公告-查询 查看记录 @author 卓大 */
    @Operation(summary = "【员工】通知公告-查询 查看记录 @author 卓大")
    @PostMapping("/oa/notice/employee/queryViewRecord")
    fun queryViewRecord(
        @Body pageBean: PageBean,
        @RequestBody noticeViewRecordQueryForm: @Valid NoticeViewRecordQueryForm?
    ): ResponseDTO<PageResult<NoticeViewRecordVO>> {
        return ResponseDTO.ok(noticeEmployeeService.queryViewRecord(pageBean, noticeViewRecordQueryForm))
    }
}