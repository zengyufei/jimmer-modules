package com.zyf.support.domain

import com.zyf.common.enums.MessageTemplateEnum
import com.zyf.common.enums.UserTypeEnum
import jakarta.validation.constraints.NotNull

/**
 * 消息发送form
 *
 * @author luoyi
 * @date 2024/06/22 20:20
 */
class MessageTemplateSendForm {
    var messageTemplateEnum: @NotNull(message = "消息子类型不能为空") MessageTemplateEnum? = null

    var receiverUserType: @NotNull(message = "接收者类型不能为空") UserTypeEnum? = null

    var receiverUserId: @NotNull(message = "接收者id不能为空") String? = null

    /**
     * 相关业务id | 可选
     * 用于跳转具体业务
     */
    var dataId: String? = null

    /**
     * 消息参数 | 可选
     * 例：订单号：【{orderId}】{time}所提交的对账单被作废，请核实信息重新提交~
     * {orderId} {time} 就是消息的参数变量
     * 发送消息时 需要在map中放入k->orderId k->time
     */
    var contentParam: Map<String, Any>? = null
}
