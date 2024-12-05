package com.zyf.support

import com.zyf.common.annotations.Body
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.utils.SmartRequestUtil
import com.zyf.service.dto.MessageQueryForm
import com.zyf.service.dto.MessageVO
import com.zyf.support.service.MessageService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

/**
 * 消息
 *
 * @author luoyi
 * @date 2024/06/22 20:20
 */
@RestController
class MessageController(
    private val messageService: MessageService
) {

    /** 分页查询我的消息 @luoyi */
    @PostMapping("/support/message/queryMyMessage")
    fun query(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: MessageQueryForm
    ): ResponseDTO<PageResult<MessageVO>?> {
        val user = SmartRequestUtil.requestUser ?: return ResponseDTO.userErrorParam("用户未登录")

        pageBean.searchCount = false
        val pageResult = messageService.query(
            pageBean, queryForm.copy(
                receiverUserId = user.userId,
                receiverUserType = user.userType?.value
            )
        )
        return ResponseDTO.ok(pageResult)
    }

    /** 查询未读消息数量 @luoyi */
    @GetMapping("/support/message/getUnreadCount")
    fun getUnreadCount(): ResponseDTO<Long?> {
        val user = SmartRequestUtil.requestUser ?: return ResponseDTO.userErrorParam("用户未登录")

        val unreadCount = user.userType?.let { user.userId?.let { it1 -> messageService.getUnreadCount(it, it1) } } ?: 0
        return ResponseDTO.ok(unreadCount)
    }

    /** 更新已读 @luoyi */
    @GetMapping("/support/message/read/{messageId}")
    fun updateReadFlag(@PathVariable messageId: String): ResponseDTO<String?> {
        val user = SmartRequestUtil.requestUser ?: return ResponseDTO.userErrorParam("用户未登录")

        user.userType?.let { user.userId?.let { it1 -> messageService.updateReadFlag(messageId, it, it1) } }
        return ResponseDTO.ok()
    }
} 