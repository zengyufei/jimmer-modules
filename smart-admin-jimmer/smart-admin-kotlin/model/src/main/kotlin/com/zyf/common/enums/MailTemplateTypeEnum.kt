package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum

/**
 * 邮件模板类型
 *
 * @Author 1024创新实验室-创始人兼主任:卓大
 * @Date 2024/8/5
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net) ，Since 2012
 */
enum class MailTemplateTypeEnum(
    @JsonValue override val value: String,
    override val desc: String
)  : BaseEnum {
    STRING("string", "字符串替代器"),

    FREEMARKER("freemarker", "freemarker模板引擎")
}