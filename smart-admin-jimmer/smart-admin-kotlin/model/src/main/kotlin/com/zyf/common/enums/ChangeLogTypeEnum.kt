package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum

/**
 * 更新类型:[1:特大版本功能更新;2:功能更新;3:bug修复]
 *
 * @Author 卓大
 * @Date 2022-09-26T14:53:50
 * @Copyright 1024创新实验室
 */

enum class ChangeLogTypeEnum(
    @JsonValue override val value: Int,
    override val desc: String
) : BaseEnum {
    /**
     * 重大更新
     */
    MAJOR_UPDATE(1, "重大更新"),

    /**
     * 功能更新
     */
    FUNCTION_UPDATE(2, "功能更新"),

    /**
     * Bug修复
     */
    BUG_FIX(2, "Bug修复"),
    ;

    companion object {

        @JsonCreator
        fun deserialize(value: Int): ChangeLogTypeEnum {
            return when (value) {
                MAJOR_UPDATE.value -> MAJOR_UPDATE
                FUNCTION_UPDATE.value -> FUNCTION_UPDATE
                BUG_FIX.value -> BUG_FIX
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }
}