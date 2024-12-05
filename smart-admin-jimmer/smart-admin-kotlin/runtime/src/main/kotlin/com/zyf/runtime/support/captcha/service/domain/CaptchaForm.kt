package com.zyf.runtime.support.captcha.service.domain

import jakarta.validation.constraints.NotBlank

/**
 * 图形验证码 表单
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021-09-02 20:21:10
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
open class CaptchaForm {
    /** 验证码 */
    @NotBlank(message = "验证码不能为空")
    var captchaCode: String? = null

    /** 验证码uuid标识 */
    @NotBlank(message = "验证码uuid标识不能为空")
    lateinit var captchaUuid: String
}
