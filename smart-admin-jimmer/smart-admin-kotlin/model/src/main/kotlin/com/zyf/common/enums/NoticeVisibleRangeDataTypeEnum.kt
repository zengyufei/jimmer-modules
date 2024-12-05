package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum

enum class NoticeVisibleRangeDataTypeEnum(
    @JsonValue override val value: Int,
    override val desc: String
) : BaseEnum {
    /**
     * 员工
     */
    EMPLOYEE(1, "员工"),

    /**
     * 部门
     */
    DEPARTMENT(2, "部门"),
    ;

    companion object {

        @JsonCreator
        fun deserialize(value: Int): NoticeVisibleRangeDataTypeEnum {
            return when (value) {
                EMPLOYEE.value -> EMPLOYEE
                DEPARTMENT.value -> DEPARTMENT
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }
}
