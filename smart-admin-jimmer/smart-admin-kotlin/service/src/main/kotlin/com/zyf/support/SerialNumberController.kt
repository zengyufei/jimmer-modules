package com.zyf.support

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.SwaggerTagConst
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.SerialNumberIdEnum
import com.zyf.common.utils.SmartEnumUtil.getEnumByValue
import com.zyf.support.domain.SerialNumberGenerateForm
import com.zyf.support.service.ISerialNumberService
import com.zyf.support.service.SerialNumberRecordService
import com.zyf.support.service.SerialNumberService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * 单据序列号
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-03-25 21:46:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Tag(name = SwaggerTagConst.Support.SERIAL_NUMBER)
@RestController
class SerialNumberController(
    val baseSerialNumberService: ISerialNumberService,
    val serialNumberService: SerialNumberService,
    val serialNumberRecordService: SerialNumberRecordService,
) {

    @Operation(summary = "生成单号 @author 卓大")
    @PostMapping("/support/serialNumber/generate")
    fun generate(@RequestBody generateForm: @Valid SerialNumberGenerateForm): ResponseDTO<List<String>?> {
        val serialNumberIdEnum: SerialNumberIdEnum = getEnumByValue<SerialNumberIdEnum>(generateForm.serialNumberId) ?: return ResponseDTO.userErrorParam("SerialNumberId，不存在" + generateForm.serialNumberId)
        return ResponseDTO.ok(baseSerialNumberService.generate(serialNumberIdEnum, generateForm.count?:1))
    }

    @get:GetMapping("/support/serialNumber/all")
    @get:Operation(summary = "获取所有单号定义 @author 卓大")
    val all: ResponseDTO<List<SerialNumber>>
        get() = ResponseDTO.ok(serialNumberService.listAll())

}
