package com.zyf.helpDoc.controller;

import com.zyf.helpDoc.HelpDocViewRecord;
import com.zyf.helpDoc.service.HelpDocViewRecordService;
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
 * 帮助文档-查看记录(HelpDocViewRecord)表控制层
 *
 * @author makejava
 * @since 2024-12-14 16:24:51
 */
@Api("HelpDocViewRecord Api")
@RestController
@OperateLog
class HelpDocViewRecordController(
    val sql: KSqlClient,
    val helpDocViewRecordService: HelpDocViewRecordService
) {


    /** 分页查询帮助文档-查看记录模块 @author makejava */
    @Operation(summary = "分页查询帮助文档-查看记录模块 @author makejava")
    @PostMapping("/helpDocViewRecord/page/query")
    fun queryByPage(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: HelpDocViewRecordQueryForm
    ): ResponseDTO<PageResult<HelpDocViewRecordVO>> {
        return helpDocViewRecordService.queryByPage(pageBean, queryForm)
    }

    /** 导出帮助文档-查看记录信息 @author makejava */
    @Operation(summary = "导出帮助文档-查看记录信息 @author makejava")
    @PostMapping("/helpDocViewRecord/exportExcel")
    fun exportExcel(@RequestBody @Valid queryForm: HelpDocViewRecordQueryForm, response: HttpServletResponse) {
        val data = helpDocViewRecordService.getExcelExportData(queryForm)
        if (data.isEmpty()) {
            SmartResponseUtil.write(response, ResponseDTO.userErrorParam<String>("暂无数据"))
            return
        }
        val watermark = "admin " + DateUtil.now()

        SmartExcelUtil.exportExcelWithWatermark(
            response,
            "帮助文档-查看记录基本信息.xlsx",
            "帮助文档-查看记录信息",
            HelpDocViewRecordExcelVO::class.java,
            data,
            watermark
        )
    }

    /** 查询帮助文档-查看记录详情 @author makejava */
    @Operation(summary = "查询帮助文档-查看记录详情 @author makejava")
    @GetMapping("/helpDocViewRecord/get/{helpDocViewRecordId}")
    fun getDetail(@PathVariable helpDocViewRecordId: String): ResponseDTO<HelpDocViewRecordDetailVO?> {
        return ResponseDTO.ok(helpDocViewRecordService.getDetail(helpDocViewRecordId))
    }

    /** 新建帮助文档-查看记录 @author makejava */
    @Operation(summary = "新建帮助文档-查看记录 @author makejava")
    @PostMapping("/helpDocViewRecord/add")
    fun add(@RequestBody @Valid addParam: HelpDocViewRecordAddParam): ResponseDTO<String?> {
        return helpDocViewRecordService.add(addParam)
    }

    /** 编辑帮助文档-查看记录 @author makejava */
    @Operation(summary = "编辑帮助文档-查看记录 @author makejava")
    @PostMapping("/helpDocViewRecord/update")
    fun update(@RequestBody @Valid updateParam: HelpDocViewRecordUpdateParam): ResponseDTO<String?> {
        return helpDocViewRecordService.update(updateParam)
    }

    /** 删除帮助文档-查看记录 @author makejava */
    @Operation(summary = "删除帮助文档-查看记录 @author makejava")
    @GetMapping("/helpDocViewRecord/delete/{helpDocViewRecordId}")
    fun delete(@PathVariable helpDocViewRecordId: String): ResponseDTO<String?> {
        return helpDocViewRecordService.deleteHelpDocViewRecord(helpDocViewRecordId)
    }

    /** 按照类型查询帮助文档-查看记录 @author makejava */
    @Operation(summary = "按照类型查询帮助文档-查看记录 @author makejava")
    @GetMapping("/helpDocViewRecord/query/list")
    fun queryList(queryForm: HelpDocViewRecordQueryForm): ResponseDTO<List<HelpDocViewRecordListVO>> {
        return helpDocViewRecordService.queryList(queryForm)
    }

}

