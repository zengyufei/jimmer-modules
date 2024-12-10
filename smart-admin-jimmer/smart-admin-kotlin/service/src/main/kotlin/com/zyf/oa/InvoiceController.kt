package com.zyf.oa

import com.zyf.common.annotations.Body
import com.zyf.common.annotations.OperateLog
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.AdminSwaggerTagConst
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.oa.service.InvoiceService
import com.zyf.service.dto.InvoiceAddForm
import com.zyf.service.dto.InvoiceQueryForm
import com.zyf.service.dto.InvoiceUpdateForm
import com.zyf.service.dto.InvoiceVO
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = AdminSwaggerTagConst.Business.OA_INVOICE)
class InvoiceController(
    private val invoiceService: InvoiceService
) {

    /** 分页查询 @author 开云 */
    @Operation(summary = "分页查询 @author 开云")
    @PostMapping("/oa/invoice/page/query")
    fun queryByPage(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: InvoiceQueryForm
    ): ResponseDTO<PageResult<InvoiceVO>> {
        return invoiceService.queryByPage(pageBean, queryForm)
    }

    /** 查询列表 @author 开云 */
    @Operation(summary = "查询列表 @author 开云")
    @GetMapping("/oa/invoice/query/list/{enterpriseId}")
    fun queryList(@PathVariable enterpriseId: String): ResponseDTO<List<InvoiceVO>> {
        return invoiceService.queryList(enterpriseId)
    }

    /** 查询发票详情 @author 开云 */
    @Operation(summary = "查询发票详情 @author 开云")
    @GetMapping("/oa/invoice/get/{invoiceId}")
    fun getDetail(@PathVariable invoiceId: String): ResponseDTO<InvoiceVO?> {
        return invoiceService.getDetail(invoiceId)
    }

    /** 新建发票 @author 开云 */
    @Operation(summary = "新建发票 @author 开云")
    @PostMapping("/oa/invoice/create")
    fun createInvoice(@RequestBody @Valid createVO: InvoiceAddForm): ResponseDTO<String?> {
        return invoiceService.createInvoice(createVO)
    }

    /** 编辑发票 @author 开云 */
    @Operation(summary = "编辑发票 @author 开云")
    @PostMapping("/oa/invoice/update")
    @OperateLog
    fun updateInvoice(@RequestBody @Valid updateVO: InvoiceUpdateForm): ResponseDTO<String?> {
        return invoiceService.updateInvoice(updateVO)
    }

    /** 删除发票 @author 开云 */
    @Operation(summary = "删除发票 @author 开云")
    @GetMapping("/oa/invoice/delete/{invoiceId}")
    fun deleteInvoice(@PathVariable invoiceId: String): ResponseDTO<String?> {
        return invoiceService.deleteInvoice(invoiceId)
    }

}