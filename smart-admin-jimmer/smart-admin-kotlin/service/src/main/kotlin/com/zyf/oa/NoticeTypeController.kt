package com.zyf.oa

import com.zyf.common.annotations.OperateLog
import com.zyf.common.annotations.Operation
import com.zyf.common.domain.ResponseDTO
import com.zyf.oa.service.NoticeTypeService
import com.zyf.service.dto.NoticeTypeVO
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
class NoticeTypeController(
    private val noticeTypeService: NoticeTypeService,
) {

    // --------------------- 通知公告类型 -------------------------

    /** 通知公告类型-获取全部 @author 卓大 */
    @Operation(summary = "通知公告类型-获取全部 @author 卓大")
    @GetMapping("/oa/noticeType/getAll")
    fun getAll(): ResponseDTO<List<NoticeTypeVO>> {
        return ResponseDTO.ok(noticeTypeService.getAll())
    }

    /** 通知公告类型-添加 @author 卓大 */
    @Operation(summary = "通知公告类型-添加 @author 卓大")
    @GetMapping("/oa/noticeType/add/{name}")
    fun add(@PathVariable name: String): ResponseDTO<String?> {
        return noticeTypeService.add(name)
    }

    /** 通知公告类型-修改 @author 卓大 */
    @Operation(summary = "通知公告类型-修改 @author 卓大")
    @GetMapping("/oa/noticeType/update/{noticeTypeId}/{name}")
    fun update(
        @PathVariable noticeTypeId: String,
        @PathVariable name: String
    ): ResponseDTO<String?> {
        return noticeTypeService.update(noticeTypeId, name)
    }

}