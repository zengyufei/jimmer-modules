package com.zyf.support;

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zyf.common.annotations.Body
import com.zyf.common.annotations.OperateLog
import com.zyf.common.annotations.Operation
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.*
import com.zyf.support.service.SerialNumberRecordService
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*


/**
 * serial_number记录表(SerialNumberRecord)表控制层
 *
 * @author makejava
 * @since 2024-12-09 20:36:31
 */
@Api("SerialNumberRecord Api")
@RestController
@OperateLog
class SerialNumberRecordController(
    val sql: KSqlClient,
    val serialNumberRecordService: SerialNumberRecordService
) {

    /** 获取生成记录 @author 卓大 */
    @Operation(summary = "获取生成记录 @author 卓大")
    @PostMapping("/support/serialNumber/queryRecord")
    fun queryRecord(
        @Body pageBean: PageBean,
        @RequestBody queryForm: @Valid SerialNumberRecordQueryForm): ResponseDTO<PageResult<SerialNumberRecordVO>> {
        return serialNumberRecordService.queryByPage(pageBean, queryForm)
    }

}

