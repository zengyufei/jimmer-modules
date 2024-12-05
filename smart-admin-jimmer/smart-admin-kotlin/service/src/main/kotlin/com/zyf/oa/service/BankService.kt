package com.zyf.oa.service

import cn.hutool.core.lang.Console.where
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.oa.*
import com.zyf.repository.RoleRepository
import com.zyf.service.dto.BankCreateForm
import com.zyf.service.dto.BankQueryForm
import com.zyf.service.dto.BankUpdateForm
import com.zyf.service.dto.BankVO
import com.zyf.system.Role
import com.zyf.system.roleId
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * OA办公-OA银行信息
 *
 * @Author 1024创新实验室:善逸
 * @Date 2022/6/23 21:59:22
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
@Slf4j
@Service
class BankService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val roleRepository: RoleRepository,
) {

    /**
     * 分页查询银行信息
     */
    fun queryByPage(pageBean: PageBean, queryForm: BankQueryForm): ResponseDTO<PageResult<BankVO>> {

        val pageResult = sql.createQuery(Bank::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(BankVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 根据企业ID查询不分页的银行列表
     */
    fun queryList(enterpriseId: String?): ResponseDTO<List<BankVO>> {
        val bankList: List<BankVO> = sql.findAll(BankVO::class) {
            where(table.enterpriseId eq enterpriseId)
        }
        return ResponseDTO.ok(bankList)
    }

    /**
     * 查询银行信息详情
     */
    fun getDetail(bankId: String?): ResponseDTO<BankVO?> {
        // 校验银行信息是否存在
        bankId ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)
        val bankVO: BankVO? = sql.findById(BankVO::class, bankId)
        bankVO ?: return ResponseDTO.userErrorParam("银行信息不存在")
        return ResponseDTO.ok(bankVO)
    }

    /**
     * 新建银行信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun createBank(createVO: BankCreateForm): ResponseDTO<String?> {
        val enterpriseId: String = createVO.enterpriseId
        // 校验企业是否存在
        sql.findById(Enterprise::class, enterpriseId) ?: return ResponseDTO.userErrorParam("企业不存在")
        // 验证银行信息账号是否重复
        val validateBank: Bank? = sql.createQuery(Bank::class) {
            where(table.enterpriseId eq enterpriseId)
            where(table.accountNumber eq createVO.accountNumber)
            select(table)
        }.fetchOneOrNull()
        validateBank?.let { return ResponseDTO.userErrorParam("银行信息账号重复") }
        // 数据插入
        sql.insert(createVO)
        // dataTracerService.addTrace(enterpriseId, DataTracerTypeEnum.OA_ENTERPRISE, "新增银行:" + DataTracerConst.HTML_BR + dataTracerService.getChangeContent(insertBank))
        return ResponseDTO.ok()
    }

    /**
     * 编辑银行信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun updateBank(updateVO: BankUpdateForm): ResponseDTO<String?> {
        val enterpriseId: String = updateVO.enterpriseId
        // 校验企业是否存在
        sql.findById(Enterprise::class, enterpriseId) ?: return ResponseDTO.userErrorParam("企业不存在")
        val bankId: String = updateVO.bankId
        // 校验银行信息是否存在
        sql.findById(Bank::class, bankId) ?: return ResponseDTO.userErrorParam("银行信息不存在")
        // 验证银行信息账号是否重复
        val validateBank: Bank? = sql.createQuery(Bank::class) {
            where(table.enterpriseId eq enterpriseId)
            where(table.accountNumber eq updateVO.accountNumber)
            where(table.bankId ne bankId)
            select(table)
        }.fetchOneOrNull()
        validateBank?.let { return ResponseDTO.userErrorParam("银行信息账号重复") }
        // 数据编辑
        sql.update(updateVO)
        // dataTracerService.addTrace(enterpriseId, DataTracerTypeEnum.OA_ENTERPRISE, "更新银行:" + DataTracerConst.HTML_BR + dataTracerService.getChangeContent(bankDetail, updateBank))
        return ResponseDTO.ok()
    }


    /**
     * 删除银行信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun deleteBank(bankId: String?): ResponseDTO<String?> {
        bankId ?: return ResponseDTO.ok()
        sql.deleteById(Bank::class, bankId)
        // dataTracerService.addTrace(bankDetail.getEnterpriseId(), DataTracerTypeEnum.OA_ENTERPRISE, "删除银行:" + DataTracerConst.HTML_BR + dataTracerService.getChangeContent(bankDetail))
        return ResponseDTO.ok()
    }
}
