package com.zyf.helpDoc

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zyf.common.annotations.Body
import com.zyf.common.annotations.Operation
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.utils.SmartRequestUtil
import com.zyf.helpDoc.service.HelpDocCatalogService
import com.zyf.helpDoc.service.HelpDocService
import com.zyf.helpDoc.service.HelpDocUserService
import com.zyf.helpDoc.service.dto.HelpDocViewRecordQueryForm
import com.zyf.helpDoc.service.dto.HelpDocViewRecordVO
import com.zyf.service.dto.*
import com.zyf.support.repeatsubmit.annoation.RepeatSubmit
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.springframework.web.bind.annotation.*

@Api("helpDoc")
@RestController
class HelpDocController(
    val helpDocService: HelpDocService,
    val helpDocCatalogService: HelpDocCatalogService,
    val helpDocUserService: HelpDocUserService,
) {


    // --------------------- 帮助文档 【目录】 -------------------------
    @Operation(summary = "帮助文档目录-获取全部 @author 卓大")
    @GetMapping("/support/helpDoc/helpDocCatalog/getAll")
    fun getAll(): ResponseDTO<List<HelpDocCatalogVO>> {
        return ResponseDTO.ok(helpDocCatalogService.all)
    }


    // --------------------- 帮助文档 【用户】-------------------------
    @Operation(summary = "【用户】帮助文档-查看详情 @author 卓大")
    @GetMapping("/support/helpDoc/user/view/{helpDocId}")
    @RepeatSubmit
    fun view(@PathVariable helpDocId: String): ResponseDTO<HelpDocDetailVO?> {
        return helpDocUserService.view(
            SmartRequestUtil.requestUser,
            helpDocId
        )
    }

    @Operation(summary = "【用户】帮助文档-查询全部 @author 卓大")
    @GetMapping("/support/helpDoc/user/queryAllHelpDocList")
    @RepeatSubmit
    fun queryAllHelpDocList(): ResponseDTO<List<HelpDocVO>> {
        return helpDocUserService.queryAllHelpDocList()
    }


    @Operation(summary = "【用户】帮助文档-查询 查看记录 @author 卓大")
    @PostMapping("/support/helpDoc/user/queryViewRecord")
    @RepeatSubmit
    fun queryViewRecord(
        @Body pageBean: PageBean,
        @RequestBody helpDocViewRecordQueryForm: @Valid HelpDocViewRecordQueryForm
    ): ResponseDTO<PageResult<HelpDocViewRecordVO>> {
        return ResponseDTO.ok(helpDocUserService.queryViewRecord(pageBean, helpDocViewRecordQueryForm))
    }

    // --------------------- 帮助文档 【目录管理】 -------------------------
    @Operation(summary = "帮助文档目录-添加 @author 卓大")
    @PostMapping("/support/helpDoc/helpDocCatalog/add")
    fun addHelpDocCatalog(@RequestBody helpDocCatalogAddForm: @Valid HelpDocCatalogAddForm): ResponseDTO<String?> {
        return helpDocCatalogService.add(helpDocCatalogAddForm)
    }

    @Operation(summary = "帮助文档目录-更新 @author 卓大")
    @PostMapping("/support/helpDoc/helpDocCatalog/update")
    fun updateHelpDocCatalog(@RequestBody helpDocCatalogUpdateForm: @Valid HelpDocCatalogUpdateForm): ResponseDTO<String?> {
        return helpDocCatalogService.update(helpDocCatalogUpdateForm)
    }

    @Operation(summary = "帮助文档目录-删除 @author 卓大")
    @GetMapping("/support/helpDoc/helpDocCatalog/delete/{helpDocCatalogId}")
    fun deleteHelpDocCatalog(@PathVariable helpDocCatalogId: String): ResponseDTO<String?> {
        return helpDocCatalogService.delete(helpDocCatalogId)
    }


    // --------------------- 帮助文档 【管理:增、删、查、改】-------------------------
    @Operation(summary = "【管理】帮助文档-分页查询 @author 卓大")
    @PostMapping("/support/helpDoc/query")
    @SaCheckPermission("support:helpDoc:query")
    fun query(
        @Body pageBean: PageBean,
        @RequestBody queryForm: @Valid HelpDocQueryForm): ResponseDTO<PageResult<HelpDocVO>> {
        return ResponseDTO.ok(helpDocService.query(pageBean, queryForm))
    }

    @Operation(summary = "【管理】帮助文档-获取详情 @author 卓大")
    @GetMapping("/support/helpDoc/getDetail/{helpDocId}")
    @SaCheckPermission("support:helpDoc:add")
    fun getDetail(@PathVariable helpDocId: String): ResponseDTO<HelpDocDetailVO?> {
        return ResponseDTO.ok(helpDocService.getDetail(helpDocId))
    }

    @Operation(summary = "【管理】帮助文档-添加 @author 卓大")
    @PostMapping("/support/helpDoc/add")
    @RepeatSubmit
    fun add(@RequestBody addForm: @Valid HelpDocAddForm): ResponseDTO<String?> {
        return helpDocService.add(addForm)
    }

    @Operation(summary = "【管理】帮助文档-更新 @author 卓大")
    @PostMapping("/support/helpDoc/update")
    @RepeatSubmit
    fun update(@RequestBody updateForm: @Valid HelpDocUpdateForm): ResponseDTO<String?> {
        return helpDocService.update(updateForm)
    }

    @Operation(summary = "【管理】帮助文档-删除 @author 卓大")
    @GetMapping("/support/helpDoc/delete/{helpDocId}")
    fun delete(@PathVariable helpDocId: String): ResponseDTO<String?> {
        return helpDocService.delete(helpDocId)
    }

    /** 【管理】帮助文档-根据关联id查询 */
    @Operation(summary = "【管理】帮助文档-根据关联id查询")
    @Api
    @GetMapping("/support/helpDoc/queryHelpDocByRelationId/{relationId}")
    fun queryHelpDocByRelationId(@PathVariable(value = "relationId") relationId: String): ResponseDTO<List<HelpDocVO?>?>? {
        return ResponseDTO.ok(helpDocService.queryHelpDocByRelationId(relationId))
    }

}