package com.zyf.support

import com.zyf.common.annotations.ApiDecrypt
import com.zyf.common.annotations.ApiEncrypt
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.SwaggerTagConst
import com.zyf.common.domain.ResponseDTO
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 *
 * api 加密
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/10/21 09:21:20
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>，Since 2012
 */
@RestController
@Tag(name = SwaggerTagConst.Support.PROTECT)
class ApiEncryptController  {

    @ApiDecrypt
    @PostMapping("/support/apiEncrypt/testRequestEncrypt")
    @Operation(summary = "测试 请求加密")
    fun testRequestEncrypt(@RequestBody @Valid form: JweForm): ResponseDTO<JweForm> {
        return ResponseDTO.ok(form)
    }

    @ApiEncrypt
    @PostMapping("/support/apiEncrypt/testResponseEncrypt")
    @Operation(summary = "测试 返回加密")
    fun testResponseEncrypt(@RequestBody @Valid form: JweForm): ResponseDTO<JweForm> {
        return ResponseDTO.ok(form)
    }

    @ApiDecrypt
    @ApiEncrypt
    @PostMapping("/support/apiEncrypt/testDecryptAndEncrypt")
    @Operation(summary = "测试 请求参数加密和解密、返回数据加密和解密")
    fun testDecryptAndEncrypt(@RequestBody @Valid form: JweForm): ResponseDTO<JweForm> {
        return ResponseDTO.ok(form)
    }

    @ApiDecrypt
    @ApiEncrypt
    @PostMapping("/support/apiEncrypt/testArray")
    @Operation(summary = "测试 数组加密和解密")
    fun testArray(@RequestBody @Valid list: List<JweForm>): ResponseDTO<List<JweForm>> {
        return ResponseDTO.ok(list)
    }

    data class JweForm(

        @field:NotBlank(message = "姓名 不能为空")
        val name: String,

        @field:NotNull
        @field:Min(1)
        val age: Int?
    )
}
