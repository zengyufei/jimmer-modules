package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType


/**
 * 性别枚举类
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2019/09/24 16:50
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class GenderEnum(
    @JsonValue override val value: Int,
    override val desc: String
) : BaseEnum {

    /**
     * 0 未知
     */
    UNKNOWN(0, "未知"),

    /**
     * 男 1 奇数为阳
     */
    MAN(1, "男"),

    /**
     * 女 2 偶数为阴
     */
    WOMAN(2, "女");


    companion object {

        @JsonCreator
        fun deserialize(value: Int): GenderEnum {
            return when(value) {
                UNKNOWN.value -> UNKNOWN
                MAN.value -> MAN
                WOMAN.value -> WOMAN
                else -> throw IllegalArgumentException("invalid value: $value")
            }
        }
    }

}
