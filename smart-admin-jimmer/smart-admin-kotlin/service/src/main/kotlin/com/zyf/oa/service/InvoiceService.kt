package com.zyf.oa.service

import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.oa.*
import com.zyf.service.dto.InvoiceAddForm
import com.zyf.service.dto.InvoiceQueryForm
import com.zyf.service.dto.InvoiceUpdateForm
import com.zyf.service.dto.InvoiceVO
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.babyfish.jimmer.sql.kt.exists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
class InvoiceService(
    val sql: KSqlClient
) {

    /**
     * 分页查询发票信息
     */
    fun queryByPage(pageBean: PageBean, queryForm: InvoiceQueryForm): ResponseDTO<PageResult<InvoiceVO>> {
        val pageResult = sql.createQuery(Invoice::class) {
            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(InvoiceVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 查询发票信息列表
     */
    fun queryList(enterpriseId: String): ResponseDTO<List<InvoiceVO>> {
        val vos = sql.createQuery(Invoice::class) {
            where(table.enterpriseId eq enterpriseId)
            where(table.disabledFlag eq false)
            select(table.fetch(InvoiceVO::class))
        }.execute()
        return ResponseDTO.ok(vos)
    }

    /**
     * 查询发票信息详情
     */
    fun getDetail(invoiceId: String): ResponseDTO<InvoiceVO?> {
        val invoiceVO = sql.findById(InvoiceVO::class, invoiceId)
            ?: return ResponseDTO.userErrorParam("发票信息不存在")
        return ResponseDTO.ok(invoiceVO)
    }

    /**
     * 新建发票信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun createInvoice(createVO: InvoiceAddForm): ResponseDTO<String?> {
        val enterpriseId = createVO.enterpriseId

        // 校验企业是否存在
        if (!sql.exists(Enterprise::class) {
            where(table.enterpriseId eq enterpriseId)
        }) {
            return ResponseDTO.userErrorParam("企业不存在")
        }

        // 验证发票信息账号是否重复
        if (sql.exists(Invoice::class) {
            where(table.enterpriseId eq enterpriseId)
            where(table.accountNumber eq createVO.accountNumber)
        }) {
            return ResponseDTO.userErrorParam("发票信息账号重复")
        }

        // 数据插入
        sql.insert(createVO)
        // TODO 变更记录
        // dataTracerService.insert(insertInvoice.invoiceId, DataTracerTypeEnum.OA_INVOICE)
        return ResponseDTO.ok()
    }

    /**
     * 编辑发票信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun updateInvoice(updateVO: InvoiceUpdateForm): ResponseDTO<String?> {
        val enterpriseId = updateVO.enterpriseId
        val invoiceId = updateVO.invoiceId

        // 校验企业是否存在
        if (!sql.exists(Enterprise::class) {
            where(table.enterpriseId eq enterpriseId)
        }) {
            return ResponseDTO.userErrorParam("企业不存在")
        }

        // 校验发票信息是否存在
        if (!sql.exists(Invoice::class) {
            where(table.invoiceId eq invoiceId)
        }) {
            return ResponseDTO.userErrorParam("发票信息不存在")
        }

        // 验证发票信息账号是否重复
        if (sql.exists(Invoice::class) {
            where(table.enterpriseId eq enterpriseId)
            where(table.accountNumber eq updateVO.accountNumber)
            where(table.invoiceId ne invoiceId)
        }) {
            return ResponseDTO.userErrorParam("发票信息账号重复")
        }

        // 数据更新
        sql.update(updateVO)
        // TODO 变更记录
        // dataTracerService.update(invoiceId, DataTracerTypeEnum.OA_INVOICE)
        return ResponseDTO.ok()
    }

    /**
     * 删除发票信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun deleteInvoice(invoiceId: String): ResponseDTO<String?> {
        // 校验发票信息是否存在
        if (!sql.exists(Invoice::class) {
            where(table.invoiceId eq invoiceId)
        }) {
            return ResponseDTO.userErrorParam("发票信息不存在")
        }

        sql.deleteById(Invoice::class, invoiceId)
        // TODO 变更记录
        // dataTracerService.delete(invoiceId, DataTracerTypeEnum.OA_INVOICE)
        return ResponseDTO.ok()
    }
}