package com.zyf.helpDoc

import com.zyf.common.annotations.Operation
import com.zyf.common.domain.ResponseDTO
import com.zyf.helpDoc.service.HelpDocService
import com.zyf.service.dto.HelpDocVo
import org.babyfish.jimmer.client.meta.Api
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@Api("helpDoc")
@RestController
class HelpDocController(
    val helpDocService: HelpDocService
) {

    /** 【管理】帮助文档-根据关联id查询 */
    @Operation(summary = "【管理】帮助文档-根据关联id查询")
    @Api
    @GetMapping("/support/helpDoc/queryHelpDocByRelationId/{relationId}")
    fun queryHelpDocByRelationId(@PathVariable(value = "relationId") relationId: String): ResponseDTO<List<HelpDocVo?>?>? {
        return ResponseDTO.ok(helpDocService.queryHelpDocByRelationId(relationId))
    }

}