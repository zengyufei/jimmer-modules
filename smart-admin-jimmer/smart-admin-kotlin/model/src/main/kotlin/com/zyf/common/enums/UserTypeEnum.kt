package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import org.babyfish.jimmer.sql.EnumType

/**
 * 用户类型
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2022/10/19 21:46:24
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class UserTypeEnum(
    @JsonValue override val value: Int,
    override val desc: String) : BaseEnum {
    /**
     * 管理端 员工用户
     */
    ADMIN_EMPLOYEE(1, "员工"),
    ;

    companion object {

        @JsonCreator
        fun deserialize(value: Int): UserTypeEnum {
            return when (value) {
                ADMIN_EMPLOYEE.value -> ADMIN_EMPLOYEE
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }
}
