package com.zyf.cfg

import cn.dev33.satoken.exception.NotPermissionException
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.base.SystemEnvironment
import com.zyf.common.code.SystemErrorCode
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.SystemEnvironmentEnum
import com.zyf.common.exception.BusinessException
import jakarta.annotation.Resource
import org.springframework.beans.TypeMismatchException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.stream.Collectors

/**
 * 全局异常拦截
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2020/8/25 21:57
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Slf4j
@ControllerAdvice
class GlobalExceptionHandler {
    @Resource
    private val systemEnvironment: SystemEnvironment? = null

    /**
     * json 格式错误 缺少请求体
     */
    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun jsonFormatExceptionHandler(e: Exception?): ResponseDTO<*> {
        if (!systemEnvironment!!.isProd) {
            log.error("全局JSON格式错误异常,URL:{}", currentRequestUrl, e)
        }
        return ResponseDTO.error<String>(UserErrorCode.PARAM_ERROR, "参数JSON格式错误")
    }

    /**
     * json 格式错误 缺少请求体
     */
    @ResponseBody
    @ExceptionHandler(TypeMismatchException::class, BindException::class)
    fun paramExceptionHandler(e: Exception?): ResponseDTO<*> {
        if (e is BindException) {
            if (e is MethodArgumentNotValidException) {
                val fieldErrors = e.bindingResult.fieldErrors
                val msgList = fieldErrors.stream().map { obj: FieldError -> obj.defaultMessage }.collect(Collectors.toList())
                return ResponseDTO.error<String>(UserErrorCode.PARAM_ERROR, java.lang.String.join(",", msgList))
            }

            val fieldErrors = e.fieldErrors
            val error = fieldErrors.stream().map { field: FieldError -> field.field + ":" + field.rejectedValue }.collect(Collectors.toList())
            val errorMsg = UserErrorCode.PARAM_ERROR.msg + ":" + error
            return ResponseDTO.error<String>(UserErrorCode.PARAM_ERROR, errorMsg)
        }
        return ResponseDTO.error<String>(UserErrorCode.PARAM_ERROR)
    }

    /**
     * sa-token 权限异常处理
     *
     * @param e 权限异常
     * @return 错误结果
     */
    @ResponseBody
    @ExceptionHandler(NotPermissionException::class)
    fun permissionException(e: NotPermissionException): ResponseDTO<String?> {
        // 开发环境 方便调试
        if (SystemEnvironmentEnum.PROD != systemEnvironment!!.currentEnvironment) {
            return ResponseDTO.error(UserErrorCode.NO_PERMISSION, e.message)
        }
        return ResponseDTO.error(UserErrorCode.NO_PERMISSION)
    }


    /**
     * 业务异常
     */
    @ResponseBody
    @ExceptionHandler(BusinessException::class)
    fun businessExceptionHandler(e: BusinessException): ResponseDTO<*> {
        if (!systemEnvironment!!.isProd) {
            log.error("全局业务异常,URL:{}", currentRequestUrl, e)
        }
        return ResponseDTO.error<String>(SystemErrorCode.SYSTEM_ERROR, e.message)
    }

    /**
     * 其他全部异常
     *
     * @param e 全局异常
     * @return 错误结果
     */
    @ResponseBody
    @ExceptionHandler(Throwable::class)
    fun errorHandler(e: Throwable): ResponseDTO<*> {
        log.error("捕获全局异常,URL:{}", currentRequestUrl, e)
        return ResponseDTO.error<String>(SystemErrorCode.SYSTEM_ERROR, if (systemEnvironment!!.isProd) null else e.toString())
    }

    private val currentRequestUrl: String?
        /**
         * 获取当前请求url
         */
        get() {
            val request = RequestContextHolder.getRequestAttributes() ?: return null
            val servletRequest = request as ServletRequestAttributes
            return servletRequest.request.requestURI
        }
}
