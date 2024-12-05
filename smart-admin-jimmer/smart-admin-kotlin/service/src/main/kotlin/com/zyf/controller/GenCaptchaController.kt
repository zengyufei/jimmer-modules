package com.zyf.controller

import com.zyf.runtime.support.captcha.service.CaptchaService
import com.zyf.runtime.support.captcha.service.domain.CaptchaVO
import org.babyfish.jimmer.client.meta.Api
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api("GenCaptcha")
@RestController
@RequestMapping("/captcha")
class GenCaptchaController(
    val captchaService: CaptchaService
) {

    @Api
    @RequestMapping("/get")
    fun getCaptchaVO(): CaptchaVO {
        return captchaService.generateCaptcha()
    }

}