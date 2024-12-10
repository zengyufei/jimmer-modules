package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import org.babyfish.jimmer.sql.EnumType

/**
 * 企业类型
 *
 * @Author 1024创新实验室: 开云
 * @Date 2022/7/28 20:37:15
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class EnterpriseTypeEnum(
    @JsonValue override val value: Int,
    override val desc: String
) : BaseEnum {
    /**
     * 有限企业
     */
    NORMAL(1, "有限企业"),

    /**
     * 外资企业
     */
    FOREIGN(2, "外资企业"),
    ;

    companion object {

        @JsonCreator
        fun deserialize(value: Int): EnterpriseTypeEnum {
            return when (value) {
                NORMAL.value -> NORMAL
                FOREIGN.value -> FOREIGN
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }
}