package com.zyf.support

import com.zyf.common.annotations.Body
import com.zyf.common.annotations.Operation
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.ConfigAddForm
import com.zyf.service.dto.ConfigQueryForm
import com.zyf.service.dto.ConfigUpdateForm
import com.zyf.service.dto.ConfigVO
import com.zyf.support.service.ConfigService
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Api("Config Api")
@RestController
class ConfigController(
    val sqlClient: KSqlClient,
    val configService: ConfigService
) {

    /** 分页查询系统配置 */
    @Operation(summary = "分页查询系统配置")
    @PostMapping("/support/config/query")
    fun queryConfigPage(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: ConfigQueryForm
    ): ResponseDTO<PageResult<ConfigVO>> {
        return ResponseDTO.ok(configService.queryConfigPage(pageBean, queryForm))
    }

    /** 添加配置参数 */
    @Operation(summary = "添加配置参数")
    @PostMapping("/support/config/add")
    fun addConfig(@RequestBody @Valid configAddForm: ConfigAddForm): ResponseDTO<String?> {
        val errorCode = configService.add(configAddForm)

        return errorCode?.let {
            ResponseDTO.error(errorCode)
        } ?: run {
            ResponseDTO.ok()
        }
    }

    /** 修改配置参数 */
    @Operation(summary = "修改配置参数")
    @PostMapping("/support/config/update")
    fun updateConfig(@RequestBody @Valid updateForm: ConfigUpdateForm): ResponseDTO<String?> {
        configService.updateConfig(updateForm)
        return ResponseDTO.ok()
    }

}