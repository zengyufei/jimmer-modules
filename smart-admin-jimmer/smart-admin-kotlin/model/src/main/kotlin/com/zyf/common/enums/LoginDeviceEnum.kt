package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum


/**
 * 登录设备类型
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021-11-29 19:48:35
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
enum class LoginDeviceEnum(
    @JsonValue override val value: Int,
    override val desc: String
) : BaseEnum {
    PC(1, "电脑端"),

    ANDROID(2, "安卓"),

    APPLE(3, "苹果"),

    H5(4, "H5"),

    WEIXIN_MP(5, "微信小程序"),
    ;
    companion object {

        @JsonCreator
        fun deserialize(value: Int): LoginDeviceEnum {
            return when (value) {
                PC.value -> PC
                ANDROID.value -> ANDROID
                APPLE.value -> APPLE
                H5.value -> H5
                WEIXIN_MP.value -> WEIXIN_MP
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }

}
