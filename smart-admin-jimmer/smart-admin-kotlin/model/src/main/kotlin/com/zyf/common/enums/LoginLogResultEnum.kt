package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import org.babyfish.jimmer.sql.EnumType


/**
 * 登录类型
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022/07/22 19:46:23
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class LoginLogResultEnum(
    @JsonValue override val value: Int,
    override val desc: String) : BaseEnum {
    LOGIN_SUCCESS(0, "登录成功"),
    LOGIN_FAIL(1, "登录失败"),
    LOGIN_OUT(2, "退出登录"),
    ;

    companion object {

        @JsonCreator
        fun deserialize(value: Int): LoginLogResultEnum {
            return when (value) {
                LOGIN_SUCCESS.value -> LOGIN_SUCCESS
                LOGIN_FAIL.value -> LOGIN_FAIL
                LOGIN_OUT.value -> LOGIN_OUT
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }
}
