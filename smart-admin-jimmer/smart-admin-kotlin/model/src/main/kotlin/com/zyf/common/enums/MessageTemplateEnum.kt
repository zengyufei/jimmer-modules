package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import org.babyfish.jimmer.sql.EnumType

@EnumType(EnumType.Strategy.ORDINAL)
enum class MessageTemplateEnum (
    @JsonValue override val value: Int,
    override val desc: String,
    val messageTypeEnum: MessageTypeEnum,
    val content: String,
): BaseEnum {
    ORDER_AUDIT(1000, "订单审批", MessageTypeEnum.ORDER, "您有一个订单等待审批，订单号【\${orderNumber}】"),
    ;
}
