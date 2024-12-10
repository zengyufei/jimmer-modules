package com.zyf.support.service

import cn.hutool.core.util.StrUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.enums.UserTypeEnum
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.common.utils.SmartBeanUtil
import com.zyf.service.dto.MessageQueryForm
import com.zyf.service.dto.MessageSendForm
import com.zyf.service.dto.MessageVO
import com.zyf.support.*
import com.zyf.support.domain.MessageTemplateSendForm
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.count
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * @author luoyi
 * @date 2024/6/27 12:14 上午
 */
@Service
class MessageService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) {

    /**
     * 分页查询 消息
     */
    fun query(pageBean: PageBean, queryForm: MessageQueryForm): PageResult<MessageVO> {
        val pageResult = sql.createQuery(Message::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.messageId.desc())

            where(queryForm)
            select(table.fetch(MessageVO::class))
        }.page(pageBean)
        return pageResult
    }

    /**
     * 查询未读消息数量
     */
    fun getUnreadCount(userType: UserTypeEnum, userId: String): Long {
        return sql.createQuery(Message::class) {
            where(table.receiverUserType eq userType.value)
            where(table.receiverUserId eq userId)
            where(table.readFlag eq false)
            select(count(table))
        }.fetchUnlimitedCount()
    }

    /**
     * 更新已读状态
     */
    fun updateReadFlag(messageId: String, userType: UserTypeEnum, receiverUserId: String) {
        sql.createUpdate(Message::class) {
            set(table.readFlag, true)
            set(table.readTime, LocalDateTime.now())
            where(table.messageId eq messageId)
            where(table.receiverUserType eq userType.value)
            where(table.receiverUserId eq receiverUserId)
            where(table.readFlag eq false)
        }.execute()
    }

    /**
     * 发送【模板消息】
     */
    fun sendTemplateMessage(vararg sendTemplateForms: MessageTemplateSendForm) {
        val sendFormList = sendTemplateForms.map { sendTemplateForm ->
            val msgTemplateTypeEnum = sendTemplateForm.messageTemplateEnum
            val content = StrUtil.format(msgTemplateTypeEnum?.content, sendTemplateForm.contentParam)

            val build = MessageSendForm.Builder()
            build.dataId(sendTemplateForm.dataId)
            msgTemplateTypeEnum?.let {
                build.title(it.desc)
            }
            build.content(content)
            sendTemplateForm.receiverUserType?.let {
                build.receiverUserType(it.value)
            }
            sendTemplateForm.receiverUserId?.let {
                build.receiverUserId(it)
            }
            msgTemplateTypeEnum?.messageTypeEnum?.let { build.messageType(it.value) }
            build.build()
        }
        sendMessage(sendFormList)
    }

    /**
     * 发送消息
     */
    fun sendMessage(vararg sendForms: MessageSendForm) {
        sendMessage(sendForms.toList())
    }

    /**
     * 批量发送通知消息
     */
    fun sendMessage(sendList: List<MessageSendForm>) {
        sendList.forEach { sendDTO ->
            SmartBeanUtil.verify(sendDTO)?.let { verify ->
                throw RuntimeException("send msg error: $verify")
            }
        }

        sql.saveEntities(sendList)
    }
} 