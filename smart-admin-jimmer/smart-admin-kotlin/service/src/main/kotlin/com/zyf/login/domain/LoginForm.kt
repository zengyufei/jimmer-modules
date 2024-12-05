package com.zyf.login.domain

import com.zyf.common.enums.LoginDeviceEnum
import com.zyf.common.valid.CheckEnum
import com.zyf.runtime.support.captcha.service.domain.CaptchaForm
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

/**
 * 员工登录
 *
 * @Author 1024创新实验室: 开云
 * @Date 2021-12-19 11:49:45
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class LoginForm : CaptchaForm() {
    /** 登录账号  */
    var loginName: @NotBlank(message = "登录账号不能为空") @Length(max = 30, message = "登录账号最多30字符") String? =
        null

    /** 密码  */
    var password: @NotBlank(message = "密码不能为空") String? = null

    /** 登录终端  */
    @CheckEnum(value = LoginDeviceEnum::class, required = true, message = "此终端不允许登录")
    var loginDevice: Int? = null

    /** 邮箱验证码  */
    var emailCode: String? = null
}
