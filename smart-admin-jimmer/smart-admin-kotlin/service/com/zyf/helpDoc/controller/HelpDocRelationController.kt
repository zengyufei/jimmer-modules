package com.zyf.helpDoc.controller;

import com.zyf.helpDoc.HelpDocRelation;
import com.zyf.helpDoc.service.HelpDocRelationService;
import cn.hutool.core.date.DateUtil
import com.zyf.common.annotations.Body
import com.zyf.common.annotations.OperateLog
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.AdminSwaggerTagConst
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.runtime.utils.SmartExcelUtil
import com.zyf.runtime.utils.SmartResponseUtil
import com.zyf.service.dto.*
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*


/**
 * 帮助文档-关联表(HelpDocRelation)表控制层
 *
 * @author makejava
 * @since 2024-12-14 16:48:23
 */
@Api("HelpDocRelation Api")
@RestController
@OperateLog
class HelpDocRelationController(
    val sql: KSqlClient,
    val helpDocRelationService: HelpDocRelationService
) {


    /** 分页查询帮助文档-关联表模块 @author makejava */
    @Operation(summary = "分页查询帮助文档-关联表模块 @author makejava")
    @PostMapping("/helpDocRelation/page/query")
    fun queryByPage(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: HelpDocRelationQueryForm
    ): ResponseDTO<PageResult<HelpDocRelationVO>> {
        return helpDocRelationService.queryByPage(pageBean, queryForm)
    }

    /** 导出帮助文档-关联表信息 @author makejava */
    @Operation(summary = "导出帮助文档-关联表信息 @author makejava")
    @PostMapping("/helpDocRelation/exportExcel")
    fun exportExcel(@RequestBody @Valid queryForm: HelpDocRelationQueryForm, response: HttpServletResponse) {
        val data = helpDocRelationService.getExcelExportData(queryForm)
        if (data.isEmpty()) {
            SmartResponseUtil.write(response, ResponseDTO.userErrorParam<String>("暂无数据"))
            return
        }
        val watermark = "admin " + DateUtil.now()

        SmartExcelUtil.exportExcelWithWatermark(
            response,
            "帮助文档-关联表基本信息.xlsx",
            "帮助文档-关联表信息",
            HelpDocRelationExcelVO::class.java,
            data,
            watermark
        )
    }

    /** 查询帮助文档-关联表详情 @author makejava */
    @Operation(summary = "查询帮助文档-关联表详情 @author makejava")
    @GetMapping("/helpDocRelation/get/{helpDocRelationId}")
    fun getDetail(@PathVariable helpDocRelationId: String): ResponseDTO<HelpDocRelationDetailVO?> {
        return ResponseDTO.ok(helpDocRelationService.getDetail(helpDocRelationId))
    }

    /** 新建帮助文档-关联表 @author makejava */
    @Operation(summary = "新建帮助文档-关联表 @author makejava")
    @PostMapping("/helpDocRelation/add")
    fun add(@RequestBody @Valid addParam: HelpDocRelationAddParam): ResponseDTO<String?> {
        return helpDocRelationService.add(addParam)
    }

    /** 编辑帮助文档-关联表 @author makejava */
    @Operation(summary = "编辑帮助文档-关联表 @author makejava")
    @PostMapping("/helpDocRelation/update")
    fun update(@RequestBody @Valid updateParam: HelpDocRelationUpdateParam): ResponseDTO<String?> {
        return helpDocRelationService.update(updateParam)
    }

    /** 删除帮助文档-关联表 @author makejava */
    @Operation(summary = "删除帮助文档-关联表 @author makejava")
    @GetMapping("/helpDocRelation/delete/{helpDocRelationId}")
    fun delete(@PathVariable helpDocRelationId: String): ResponseDTO<String?> {
        return helpDocRelationService.deleteHelpDocRelation(helpDocRelationId)
    }

    /** 按照类型查询帮助文档-关联表 @author makejava */
    @Operation(summary = "按照类型查询帮助文档-关联表 @author makejava")
    @GetMapping("/helpDocRelation/query/list")
    fun queryList(queryForm: HelpDocRelationQueryForm): ResponseDTO<List<HelpDocRelationListVO>> {
        return helpDocRelationService.queryList(queryForm)
    }

}

