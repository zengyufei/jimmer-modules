package com.zyf.support;

import com.zyf.common.annotations.OperateLog
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.SwaggerTagConst
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.ConfigKeyEnum
import com.zyf.support.domain.Level3ProtectConfigForm
import com.zyf.support.service.ConfigService
import com.zyf.support.service.Level3ProtectConfigService
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


/**
 * 登录失败次数记录表(LoginFail)表控制层
 *
 * @author makejava
 * @since 2024-12-09 16:44:35
 */
@Api("level3protect Api")
@RestController
@OperateLog
@Tag(name = SwaggerTagConst.Support.PROTECT)
class Level3protectController(
    val sql: KSqlClient,
    var level3ProtectConfigService: Level3ProtectConfigService,
    var configService: ConfigService,
) {


    /** 更新三级等保配置 @author 1024创新实验室-主任-卓大 */
    @Operation(summary = "更新三级等保配置 @author 1024创新实验室-主任-卓大")
    @PostMapping("/support/protect/level3protect/updateConfig")
    fun updateConfig(@RequestBody configForm: @Valid Level3ProtectConfigForm): ResponseDTO<String?> {
        return level3ProtectConfigService.updateLevel3Config(configForm)
    }

    /** 查询 三级等保配置 @author 1024创新实验室-主任-卓大 */
    @Operation(summary = "查询 三级等保配置 @author 1024创新实验室-主任-卓大")
    @GetMapping("/support/protect/level3protect/getConfig")
    fun getConfig(): ResponseDTO<String?> {
        return ResponseDTO.ok(configService.getConfigValue(ConfigKeyEnum.LEVEL3_PROTECT_CONFIG))
    }
}

