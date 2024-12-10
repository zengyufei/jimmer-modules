package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import org.babyfish.jimmer.sql.EnumType

@EnumType(EnumType.Strategy.ORDINAL)
enum class CategoryTypeEnum (
    @JsonValue override val value: Int,
    override val desc: String
) : BaseEnum {
    /**
     * 1 商品
     */
    GOODS(1, "商品"),

    /**
     * 2 自定义
     */
    CUSTOM(2, "自定义"),
    ;
}
