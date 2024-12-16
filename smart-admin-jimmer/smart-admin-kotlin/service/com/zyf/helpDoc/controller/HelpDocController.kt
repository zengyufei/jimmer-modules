package com.zyf.helpDoc.controller;

import com.zyf.helpDoc.HelpDoc;
import com.zyf.helpDoc.service.HelpDocService;
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
 * 帮助文档(HelpDoc)表控制层
 *
 * @author makejava
 * @since 2024-12-14 16:48:23
 */
@Api("HelpDoc Api")
@RestController
@OperateLog
class HelpDocController(
    val sql: KSqlClient,
    val helpDocService: HelpDocService
) {


    /** 分页查询帮助文档模块 @author makejava */
    @Operation(summary = "分页查询帮助文档模块 @author makejava")
    @PostMapping("/helpDoc/page/query")
    fun queryByPage(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: HelpDocQueryForm
    ): ResponseDTO<PageResult<HelpDocVO>> {
        return helpDocService.queryByPage(pageBean, queryForm)
    }

    /** 导出帮助文档信息 @author makejava */
    @Operation(summary = "导出帮助文档信息 @author makejava")
    @PostMapping("/helpDoc/exportExcel")
    fun exportExcel(@RequestBody @Valid queryForm: HelpDocQueryForm, response: HttpServletResponse) {
        val data = helpDocService.getExcelExportData(queryForm)
        if (data.isEmpty()) {
            SmartResponseUtil.write(response, ResponseDTO.userErrorParam<String>("暂无数据"))
            return
        }
        val watermark = "admin " + DateUtil.now()

        SmartExcelUtil.exportExcelWithWatermark(
            response,
            "帮助文档基本信息.xlsx",
            "帮助文档信息",
            HelpDocExcelVO::class.java,
            data,
            watermark
        )
    }

    /** 查询帮助文档详情 @author makejava */
    @Operation(summary = "查询帮助文档详情 @author makejava")
    @GetMapping("/helpDoc/get/{helpDocId}")
    fun getDetail(@PathVariable helpDocId: String): ResponseDTO<HelpDocDetailVO?> {
        return ResponseDTO.ok(helpDocService.getDetail(helpDocId))
    }

    /** 新建帮助文档 @author makejava */
    @Operation(summary = "新建帮助文档 @author makejava")
    @PostMapping("/helpDoc/add")
    fun add(@RequestBody @Valid addParam: HelpDocAddParam): ResponseDTO<String?> {
        return helpDocService.add(addParam)
    }

    /** 编辑帮助文档 @author makejava */
    @Operation(summary = "编辑帮助文档 @author makejava")
    @PostMapping("/helpDoc/update")
    fun update(@RequestBody @Valid updateParam: HelpDocUpdateParam): ResponseDTO<String?> {
        return helpDocService.update(updateParam)
    }

    /** 删除帮助文档 @author makejava */
    @Operation(summary = "删除帮助文档 @author makejava")
    @GetMapping("/helpDoc/delete/{helpDocId}")
    fun delete(@PathVariable helpDocId: String): ResponseDTO<String?> {
        return helpDocService.deleteHelpDoc(helpDocId)
    }

    /** 按照类型查询帮助文档 @author makejava */
    @Operation(summary = "按照类型查询帮助文档 @author makejava")
    @GetMapping("/helpDoc/query/list")
    fun queryList(queryForm: HelpDocQueryForm): ResponseDTO<List<HelpDocListVO>> {
        return helpDocService.queryList(queryForm)
    }

}

