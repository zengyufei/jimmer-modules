package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import org.babyfish.jimmer.sql.EnumType

@EnumType(EnumType.Strategy.ORDINAL)
enum class DataScopeTypeEnum(
    @JsonValue override val value: Int,
    val sort: Int,
    val enumName: String,
    override val desc: String
) : BaseEnum {

    /**
     * 系统通知
     */
    NOTICE(1, 20, "系统通知", "系统通知数据范围"),
    ;

    companion object {

        @JsonCreator
        fun deserialize(value: Int): DataScopeTypeEnum {
            return when (value) {
                NOTICE.value -> NOTICE
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }
}
