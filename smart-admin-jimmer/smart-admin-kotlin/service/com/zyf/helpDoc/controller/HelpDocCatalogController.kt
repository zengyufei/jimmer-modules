package com.zyf.helpDoc.controller;

import com.zyf.helpDoc.HelpDocCatalog;
import com.zyf.helpDoc.service.HelpDocCatalogService;
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
 * 帮助文档-目录(HelpDocCatalog)表控制层
 *
 * @author makejava
 * @since 2024-12-14 16:48:23
 */
@Api("HelpDocCatalog Api")
@RestController
@OperateLog
class HelpDocCatalogController(
    val sql: KSqlClient,
    val helpDocCatalogService: HelpDocCatalogService
) {


    /** 分页查询帮助文档-目录模块 @author makejava */
    @Operation(summary = "分页查询帮助文档-目录模块 @author makejava")
    @PostMapping("/helpDocCatalog/page/query")
    fun queryByPage(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: HelpDocCatalogQueryForm
    ): ResponseDTO<PageResult<HelpDocCatalogVO>> {
        return helpDocCatalogService.queryByPage(pageBean, queryForm)
    }

    /** 导出帮助文档-目录信息 @author makejava */
    @Operation(summary = "导出帮助文档-目录信息 @author makejava")
    @PostMapping("/helpDocCatalog/exportExcel")
    fun exportExcel(@RequestBody @Valid queryForm: HelpDocCatalogQueryForm, response: HttpServletResponse) {
        val data = helpDocCatalogService.getExcelExportData(queryForm)
        if (data.isEmpty()) {
            SmartResponseUtil.write(response, ResponseDTO.userErrorParam<String>("暂无数据"))
            return
        }
        val watermark = "admin " + DateUtil.now()

        SmartExcelUtil.exportExcelWithWatermark(
            response,
            "帮助文档-目录基本信息.xlsx",
            "帮助文档-目录信息",
            HelpDocCatalogExcelVO::class.java,
            data,
            watermark
        )
    }

    /** 查询帮助文档-目录详情 @author makejava */
    @Operation(summary = "查询帮助文档-目录详情 @author makejava")
    @GetMapping("/helpDocCatalog/get/{helpDocCatalogId}")
    fun getDetail(@PathVariable helpDocCatalogId: String): ResponseDTO<HelpDocCatalogDetailVO?> {
        return ResponseDTO.ok(helpDocCatalogService.getDetail(helpDocCatalogId))
    }

    /** 新建帮助文档-目录 @author makejava */
    @Operation(summary = "新建帮助文档-目录 @author makejava")
    @PostMapping("/helpDocCatalog/add")
    fun add(@RequestBody @Valid addParam: HelpDocCatalogAddParam): ResponseDTO<String?> {
        return helpDocCatalogService.add(addParam)
    }

    /** 编辑帮助文档-目录 @author makejava */
    @Operation(summary = "编辑帮助文档-目录 @author makejava")
    @PostMapping("/helpDocCatalog/update")
    fun update(@RequestBody @Valid updateParam: HelpDocCatalogUpdateParam): ResponseDTO<String?> {
        return helpDocCatalogService.update(updateParam)
    }

    /** 删除帮助文档-目录 @author makejava */
    @Operation(summary = "删除帮助文档-目录 @author makejava")
    @GetMapping("/helpDocCatalog/delete/{helpDocCatalogId}")
    fun delete(@PathVariable helpDocCatalogId: String): ResponseDTO<String?> {
        return helpDocCatalogService.deleteHelpDocCatalog(helpDocCatalogId)
    }

    /** 按照类型查询帮助文档-目录 @author makejava */
    @Operation(summary = "按照类型查询帮助文档-目录 @author makejava")
    @GetMapping("/helpDocCatalog/query/list")
    fun queryList(queryForm: HelpDocCatalogQueryForm): ResponseDTO<List<HelpDocCatalogListVO>> {
        return helpDocCatalogService.queryList(queryForm)
    }

}

