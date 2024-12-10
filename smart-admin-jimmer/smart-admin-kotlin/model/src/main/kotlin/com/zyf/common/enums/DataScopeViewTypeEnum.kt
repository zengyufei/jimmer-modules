package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import org.babyfish.jimmer.sql.EnumType

/**
 * 数据范围 种类
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020/11/28  20:59:17
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class DataScopeViewTypeEnum(
    @JsonValue override val value: Int,
    val level: Int,
    override val desc: String
) : BaseEnum {
    /**
     * 本人
     */
    ME(0, 0, "本人"),

    /**
     * 部门
     */
    DEPARTMENT(1, 5, "本部门"),

    /**
     * 本部门及下属子部门
     */
    DEPARTMENT_AND_SUB(2, 10, "本部门及下属子部门"),

    /**
     * 全部
     */
    ALL(10, 100, "全部"),
;
    companion object {

        @JsonCreator
        fun deserialize(value: Int): DataScopeViewTypeEnum {
            return when (value) {
                ME.value -> ME
                DEPARTMENT.value -> DEPARTMENT
                DEPARTMENT_AND_SUB.value -> DEPARTMENT_AND_SUB
                ALL.value -> ALL
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }

}
