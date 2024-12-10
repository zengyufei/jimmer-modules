package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum

/**
 * 单据序列号 枚举
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-03-25 21:46:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
enum class SerialNumberIdEnum(
    @JsonValue override val value: String,
    override val desc: String
) : BaseEnum {
    ORDER("1", "订单id"),

    CONTRACT("2", "合同id"),
    ;

}
