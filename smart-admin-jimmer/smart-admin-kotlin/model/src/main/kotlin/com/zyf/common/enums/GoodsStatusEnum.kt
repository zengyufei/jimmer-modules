package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import org.babyfish.jimmer.sql.EnumType

@EnumType(EnumType.Strategy.ORDINAL)
enum class GoodsStatusEnum (
    @JsonValue override val value: Int,
    override val desc: String
) : BaseEnum {
    /**
     * 1 预约中
     */
    APPOINTMENT(1, "预约中"),

    /**
     * 2 售卖
     */
    SELL(2, "售卖中"),

    /**
     * 3 售罄
     */
    SELL_OUT(3, "售罄"),
    ;
    /**
     * 声明class即可,不需要有具体实现
     */

}