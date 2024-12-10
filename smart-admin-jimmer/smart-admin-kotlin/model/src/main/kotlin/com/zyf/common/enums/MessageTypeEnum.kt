package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import org.babyfish.jimmer.sql.EnumType


/**
 * 消息类型
 *
 * @author luoyi
 * @date 2024/06/22 20:20
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class MessageTypeEnum (
    @JsonValue override val value: Int,
    override val desc: String,
): BaseEnum {
    MAIL(1, "站内信"),

    ORDER(2, "订单"),
    ;
}
