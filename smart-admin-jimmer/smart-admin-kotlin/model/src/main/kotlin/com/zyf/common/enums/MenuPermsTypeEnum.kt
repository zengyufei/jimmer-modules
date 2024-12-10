package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import org.babyfish.jimmer.sql.EnumType


/**
 * 权限类型
 *
 * @Author 1024创新实验室: 善逸
 * @Date 2022-03-06 22:04:37
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class MenuPermsTypeEnum(
    @JsonValue override val value: Int,
    override val desc: String
) : BaseEnum {
    /**
     * sa-token
     */
    SA_TOKEN(1, "Sa-Token模式"),
    ;

    companion object {

        @JsonCreator
        fun deserialize(value: Int): MenuPermsTypeEnum {
            return when (value) {
                SA_TOKEN.value -> SA_TOKEN
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }
}
