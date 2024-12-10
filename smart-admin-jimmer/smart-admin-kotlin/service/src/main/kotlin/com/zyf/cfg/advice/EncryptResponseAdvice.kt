package com.zyf.cfg.advice

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.ApiEncrypt
import com.zyf.common.annotations.Slf4j
import com.zyf.common.code.DataTypeEnum
import com.zyf.common.domain.ResponseDTO
import com.zyf.support.service.ApiEncryptService
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

/**
 * 加密
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/10/24 09:52:58
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)，Since 2012
 */
@Slf4j
@ControllerAdvice
class EncryptResponseAdvice(
    val apiEncryptService: ApiEncryptService,
    val objectMapper: ObjectMapper,
) : ResponseBodyAdvice<ResponseDTO<*>> {

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return returnType.hasMethodAnnotation(ApiEncrypt::class.java) || returnType.containingClass.isAnnotationPresent(ApiEncrypt::class.java)
    }


    override fun beforeBodyWrite(body: ResponseDTO<*>?, returnType: MethodParameter, selectedContentType: MediaType, selectedConverterType: Class<out HttpMessageConverter<*>>, request: ServerHttpRequest, response: ServerHttpResponse)
            : ResponseDTO<*>? {
        if (body?.data == null) {
            return body
        }

        try {
            val encrypt: String = apiEncryptService.encrypt(objectMapper.writeValueAsString(body.data))
            val responseDTO = ResponseDTO(body.code, body.level, body.ok, body.msg, encrypt)
            responseDTO.dataType = DataTypeEnum.ENCRYPT.value
            return responseDTO
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }
}


