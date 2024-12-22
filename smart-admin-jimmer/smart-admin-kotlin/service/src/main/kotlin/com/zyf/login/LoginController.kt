package com.zyf.login

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.extra.servlet.JakartaServletUtil
import com.zyf.common.annotations.NoNeedLogin
import com.zyf.common.annotations.Operation
import com.zyf.common.constant.RequestHeaderConst
import com.zyf.common.domain.ResponseDTO
import com.zyf.login.domain.LoginForm
import com.zyf.login.domain.LoginResultVO
import com.zyf.login.service.LoginService
import com.zyf.runtime.support.captcha.service.CaptchaService
import com.zyf.runtime.support.captcha.service.domain.CaptchaVO
import com.zyf.support.service.Level3ProtectConfigService
import com.zyf.utils.AdminRequestUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.springframework.web.bind.annotation.*

@Api("Login")
@RestController
class LoginController(
    val captchaService: CaptchaService,
    val loginService: LoginService,
    var level3ProtectConfigService: Level3ProtectConfigService,
) {


    @Api
    @NoNeedLogin
    @PostMapping("/login")
    /** 登录 @author 卓大 */
    @Operation(summary = "登录 @author 卓大")
    fun login(@RequestBody loginForm: @Valid LoginForm?, request: HttpServletRequest?): ResponseDTO<LoginResultVO?> {
        val ip = JakartaServletUtil.getClientIP(request)
        val userAgent = JakartaServletUtil.getHeaderIgnoreCase(request, RequestHeaderConst.USER_AGENT)
        return loginService.login(loginForm!!, ip, userAgent)
    }

    @GetMapping("/login/getLoginInfo")
    /** 获取登录结果信息  @author 卓大 */
    @Operation(summary = "获取登录结果信息  @author 卓大")
    fun getLoginInfo(): ResponseDTO<LoginResultVO?> {
        val tokenValue: String = StpUtil.getTokenValue()
        val loginResult: LoginResultVO = loginService.getLoginResult(AdminRequestUtil.requestUser!!, tokenValue)
        loginResult.token = tokenValue
        return ResponseDTO.ok(loginResult)
    }

    /** 退出登陆  @author 卓大 */
    @Operation(summary = "退出登陆  @author 卓大")
    @GetMapping("/login/logout")
    fun logout(@RequestHeader(value = RequestHeaderConst.TOKEN, required = false) token: String?): ResponseDTO<String?> {
        return loginService.logout(token, AdminRequestUtil.requestUser)
    }

    @Api
    /** 获取验证码  @author 卓大 */
    @Operation(summary = "获取验证码  @author 卓大")
    @GetMapping("/login/getCaptcha")
    @NoNeedLogin
    fun getCaptcha(): ResponseDTO<CaptchaVO> {
        return ResponseDTO.ok(captchaService.generateCaptcha())
    }

    @NoNeedLogin
    @GetMapping("/login/sendEmailCode/{loginName}")
    /** 获取邮箱登录验证码 @author 卓大 */
    @Operation(summary = "获取邮箱登录验证码 @author 卓大")
    fun sendEmailCode(@PathVariable loginName: String?): ResponseDTO<String?> {
        return loginService.sendEmailCode(loginName)
    }


    @NoNeedLogin
    @GetMapping("/login/getTwoFactorLoginFlag")
    /** 获取双因子登录标识 @author 卓大 */
    @Operation(summary = "获取双因子登录标识 @author 卓大")
    fun getTwoFactorLoginFlag(): ResponseDTO<Boolean> {
        // 双因子登录
        val twoFactorLoginEnabled: Boolean = level3ProtectConfigService.isTwoFactorLoginEnabled
        return ResponseDTO.ok(twoFactorLoginEnabled)
    }
}