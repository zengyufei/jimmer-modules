package com.zyf.cfg.interceptor

import cn.dev33.satoken.annotation.SaIgnore
import cn.dev33.satoken.exception.SaTokenException
import cn.dev33.satoken.stp.StpUtil
import cn.dev33.satoken.strategy.SaStrategy
import cn.hutool.core.util.StrUtil
import com.zyf.common.annotations.NoNeedLogin
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.base.SystemEnvironment
import com.zyf.common.code.SystemErrorCode
import com.zyf.common.code.UserErrorCode
import com.zyf.common.constant.StringConst
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.SystemEnvironmentEnum
import com.zyf.common.enums.UserTypeEnum
import com.zyf.common.utils.SmartRequestUtil.remove
import com.zyf.common.utils.SmartRequestUtil.requestUser
import com.zyf.login.domain.RequestEmployee
import com.zyf.login.service.LoginService
import com.zyf.runtime.utils.SmartResponseUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

/**
 * admin 拦截器
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/7/26 20:20:33
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)，Since 2012
 */
@Component
@Slf4j
class AdminInterceptor(
    val loginService: LoginService,
    val systemEnvironment: SystemEnvironment,
) : HandlerInterceptor {


    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // OPTIONS请求直接return

        if (HttpMethod.OPTIONS.toString() == request.method) {
            response.status = HttpStatus.NO_CONTENT.value()
            return false
        }

        val isHandler = handler is HandlerMethod
        if (!isHandler) {
            return true
        }

        try {
            // --------------- 第一步： 根据token 获取用户 ---------------

            val tokenValue = StpUtil.getTokenValue()
            val debugNumberTokenFlag = isDevDebugNumberToken(tokenValue)

            val loginId: String?
            if (debugNumberTokenFlag) {
                // 开发、测试环境，且为数字的话，则表明为 调试临时用户，即需要调用 sa-token switch
                loginId = UserTypeEnum.ADMIN_EMPLOYEE.value.toString() + StringConst.COLON + tokenValue
                StpUtil.switchTo(loginId)
            } else {
                loginId = StpUtil.getLoginIdByToken(tokenValue) as String?
            }

            val requestEmployee = loginService.getLoginEmployee(loginId, request)

            // --------------- 第二步： 校验 登录 ---------------
            val method = (handler as HandlerMethod).method
            val noNeedLogin = handler.getMethodAnnotation(NoNeedLogin::class.java)
            if (noNeedLogin != null) {
                checkActiveTimeout(requestEmployee, debugNumberTokenFlag)
                return true
            }

            if (requestEmployee == null) {
                SmartResponseUtil.write(response, ResponseDTO.error<String>(UserErrorCode.LOGIN_STATE_INVALID))
                return false
            }

            // 检测token 活跃频率
            checkActiveTimeout(requestEmployee, debugNumberTokenFlag)


            // --------------- 第三步： 校验 权限 ---------------
            requestUser = requestEmployee
            if (SaStrategy.instance.isAnnotationPresent.apply(method, SaIgnore::class.java)) {
                return true
            }

            // 如果是超级管理员的话，不需要校验权限
            if (requestEmployee.administratorFlag) {
                return true
            }

            SaStrategy.instance.checkMethodAnnotation.accept(method)
        } catch (e: SaTokenException) {
            /*
             * sa-token 异常状态码
             * 具体请看： https://sa-token.cc/doc.html#/fun/exception-code
             */
            val code = e.code
            if (code == 11041 || code == 11051) {
                SmartResponseUtil.write(response, ResponseDTO.error<String>(UserErrorCode.NO_PERMISSION))
            } else if (code == 11016) {
                SmartResponseUtil.write(response, ResponseDTO.error<String>(UserErrorCode.LOGIN_ACTIVE_TIMEOUT))
            } else if (code >= 11011 && code <= 11015) {
                SmartResponseUtil.write(response, ResponseDTO.error<String>(UserErrorCode.LOGIN_STATE_INVALID))
            } else {
                SmartResponseUtil.write(response, ResponseDTO.error<String>(UserErrorCode.PARAM_ERROR))
            }
            return false
        } catch (e: Throwable) {
            SmartResponseUtil.write(response, ResponseDTO.error<String>(SystemErrorCode.SYSTEM_ERROR))
            log.error(e.message, e)
            return false
        }

        // 通过验证
        return true
    }


    /**
     * 检测：token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结
     */
    private fun checkActiveTimeout(requestEmployee: RequestEmployee?, debugNumberTokenFlag: Boolean) {
        // 对于开发环境的 数字 debug token ，不需要检测活跃有效期

        if (debugNumberTokenFlag) {
            return
        }

        // 用户不在线，也不用检测
        if (requestEmployee == null) {
            return
        }

        StpUtil.checkActiveTimeout()
        StpUtil.updateLastActiveToNow()
    }


    /**
     * 是否为开发使用的 debug token
     *
     * @param token
     * @return
     */
    private fun isDevDebugNumberToken(token: String?): Boolean {
        if (!StrUtil.isNumeric(token)) {
            return false
        }
        return (systemEnvironment.currentEnvironment == SystemEnvironmentEnum.DEV
                || systemEnvironment.currentEnvironment == SystemEnvironmentEnum.TEST)
    }

    @Throws(Exception::class)
    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        // 清除上下文
        remove()
        // 开发环境，关闭 sa token 的临时切换用户
        if (systemEnvironment.currentEnvironment == SystemEnvironmentEnum.DEV) {
            StpUtil.endSwitch()
        }
    }
}