package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import org.babyfish.jimmer.sql.EnumType

/**
 * 数据范围 sql where
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020/11/28  20:59:17
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class DataScopeWhereInTypeEnum(
    @JsonValue override val value: Int,
    override val desc: String
) : BaseEnum {
    /**
     * 以员工IN
     */
    EMPLOYEE(0, "以员工IN"),

    /**
     * 以部门IN
     */
    DEPARTMENT(1, "以部门IN"),

    /**
     * 自定义策略
     */
    CUSTOM_STRATEGY(2, "自定义策略"),
    ;

    companion object {

        @JsonCreator
        fun deserialize(value: Int): DataScopeWhereInTypeEnum {
            return when (value) {
                EMPLOYEE.value -> EMPLOYEE
                DEPARTMENT.value -> DEPARTMENT
                CUSTOM_STRATEGY.value -> CUSTOM_STRATEGY
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }
}
