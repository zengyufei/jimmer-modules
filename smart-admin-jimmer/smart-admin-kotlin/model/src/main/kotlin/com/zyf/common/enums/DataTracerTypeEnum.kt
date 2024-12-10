package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import com.zyf.common.enums.EnterpriseTypeEnum.FOREIGN
import com.zyf.common.enums.EnterpriseTypeEnum.NORMAL
import org.babyfish.jimmer.sql.EnumType

@EnumType(EnumType.Strategy.ORDINAL)
enum class DataTracerTypeEnum(
    @JsonValue override val value: Int,
    override val desc: String
) : BaseEnum {
    /**
     * 商品
     */
    GOODS(1, "商品"),

    /**
     * 通知公告
     */
    OA_NOTICE(2, "OA-通知公告"),

    /**
     * 企业信息
     */
    OA_ENTERPRISE(3, "OA-企业信息"),
    ;

    companion object {

        @JsonCreator
        fun deserialize(value: Int): DataTracerTypeEnum {
            return when (value) {
                GOODS.value -> GOODS
                OA_NOTICE.value -> OA_NOTICE
                OA_ENTERPRISE.value -> OA_ENTERPRISE
                else -> throw IllegalArgumentException("Unexpected value: $value")
            }
        }
    }
}
