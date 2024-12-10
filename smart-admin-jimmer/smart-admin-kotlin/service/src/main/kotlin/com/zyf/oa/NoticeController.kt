package com.zyf.oa

import com.zyf.common.annotations.Body
import com.zyf.common.annotations.OperateLog
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.AdminSwaggerTagConst
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.oa.service.NoticeService
import com.zyf.service.dto.*
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
@Tag(name = AdminSwaggerTagConst.Business.OA_NOTICE)
@RestController
@OperateLog
class NoticeController(
    private val noticeService: NoticeService,
) {

    // --------------------- 【管理】通知公告-------------------------

    /** 【管理】通知公告-分页查询 @author 卓大 */
    @Operation(summary = "【管理】通知公告-分页查询 @author 卓大")
    @PostMapping("/oa/notice/query")
    fun query(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: NoticeQueryForm
    ): ResponseDTO<PageResult<NoticeVO>> {
        return ResponseDTO.ok(noticeService.query(pageBean, queryForm))
    }

    /** 【管理】通知公告-添加 @author 卓大 */
    @Operation(summary = "【管理】通知公告-添加 @author 卓大")
    @PostMapping("/oa/notice/add")
    fun add(@RequestBody @Valid addForm: NoticeAddForm): ResponseDTO<String?> {
        return noticeService.add(addForm)
    }

    /** 【管理】通知公告-更新 @author 卓大 */
    @Operation(summary = "【管理】通知公告-更新 @author 卓大")
    @PostMapping("/oa/notice/update")
    fun update(@RequestBody @Valid updateForm: NoticeUpdateForm): ResponseDTO<String?> {
        return noticeService.update(updateForm)
    }

    /** 【管理】通知公告-更新详情 @author 卓大 */
    @Operation(summary = "【管理】通知公告-更新详情 @author 卓大")
    @GetMapping("/oa/notice/getUpdateVO/{noticeId}")
    fun getUpdateFormVO(@PathVariable noticeId: String): ResponseDTO<NoticeUpdateFormVO?> {
        return ResponseDTO.ok(noticeService.getUpdateFormVO(noticeId))
    }

    /** 【管理】通知公告-删除 @author 卓大 */
    @Operation(summary = "【管理】通知公告-删除 @author 卓大")
    @GetMapping("/oa/notice/delete/{noticeId}")
    fun delete(@PathVariable noticeId: String): ResponseDTO<String?> {
        return noticeService.delete(noticeId)
    }
}