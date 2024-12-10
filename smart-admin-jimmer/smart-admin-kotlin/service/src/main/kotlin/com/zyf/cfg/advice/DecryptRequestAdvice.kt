package com.zyf.cfg.advice

import cn.hutool.core.util.StrUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.ApiDecrypt
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.support.service.ApiEncryptService
import org.apache.commons.io.IOUtils
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter
import java.io.InputStream
import java.lang.reflect.Type

/**
 * 解密
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/10/21 11:41:46
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)，Since 2012
 */
@Slf4j
@ControllerAdvice
class DecryptRequestAdvice(
    val apiEncryptService: ApiEncryptService,
    val objectMapper: ObjectMapper,
) : RequestBodyAdviceAdapter() {

    override fun supports(methodParameter: MethodParameter, targetType: Type, converterType: Class<out HttpMessageConverter<*>?>): Boolean {
        return methodParameter.hasMethodAnnotation(ApiDecrypt::class.java) || methodParameter.hasParameterAnnotation(ApiDecrypt::class.java) || methodParameter.containingClass.isAnnotationPresent(ApiDecrypt::class.java)
    }

    override fun beforeBodyRead(inputMessage: HttpInputMessage, parameter: MethodParameter, targetType: Type, converterType: Class<out HttpMessageConverter<*>?>)
            : HttpInputMessage {
        try {
            val bodyStr = IOUtils.toString(inputMessage.body, ENCODING)
            val apiEncryptForm = objectMapper.readValue(bodyStr, ApiEncryptForm::class.java)
            if (StrUtil.isBlank(apiEncryptForm.encryptData)) {
                return inputMessage
            }
            val decrypt = apiEncryptService.decrypt(apiEncryptForm.encryptData)
            return DecryptHttpInputMessage(inputMessage.headers, IOUtils.toInputStream(decrypt, ENCODING))
        } catch (e: Exception) {
            log.error("", e)
            return inputMessage
        }
    }

    override fun afterBodyRead(body: Any, inputMessage: HttpInputMessage, parameter: MethodParameter, targetType: Type, converterType: Class<out HttpMessageConverter<*>?>): Any {
        return body
    }

    override fun handleEmptyBody(body: Any?, inputMessage: HttpInputMessage, parameter: MethodParameter, targetType: Type, converterType: Class<out HttpMessageConverter<*>?>): Any? {
        return body
    }

    internal class DecryptHttpInputMessage(private val headers: HttpHeaders, private val body: InputStream) : HttpInputMessage {
        override fun getBody(): InputStream {
            return body
        }

        override fun getHeaders(): HttpHeaders {
            return headers
        }
    }

    companion object {
        private const val ENCODING = "UTF-8"
    }
}
