package com.zyf.oa

import com.zyf.common.annotations.Body
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.oa.service.BankService
import com.zyf.service.dto.BankCreateForm
import com.zyf.service.dto.BankQueryForm
import com.zyf.service.dto.BankUpdateForm
import com.zyf.service.dto.BankVO
import jakarta.validation.Valid
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*

/**
 * OA办公-OA银行信息
 *
 * @Author 1024创新实验室:善逸
 * @Date 2022/6/23 21:59:22
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
@RestController
class BankController(
    val sql: KSqlClient,
    val bankService: BankService,
) {

    /** 分页查询银行信息 @author 善逸 */
    @PostMapping("/oa/bank/page/query")
    fun queryByPage(
        @Body pageBean: PageBean,
        @RequestBody queryForm: @Valid BankQueryForm): ResponseDTO<PageResult<BankVO>> {
        return bankService.queryByPage(pageBean, queryForm)
    }

    /** 根据企业ID查询银行信息列表 @author 善逸 */
    @GetMapping("/oa/bank/query/list/{enterpriseId}")
    fun queryList(@PathVariable enterpriseId: String?): ResponseDTO<List<BankVO>> {
        return bankService.queryList(enterpriseId)
    }

    /** 查询银行信息详情 @author 善逸 */
    @GetMapping("/oa/bank/get/{bankId}")
    fun getDetail(@PathVariable bankId: String?): ResponseDTO<BankVO?> {
        return bankService.getDetail(bankId)
    }

    /** 新建银行信息 @author 善逸 */
    @PostMapping("/oa/bank/create")
    fun createBank(@RequestBody createVO: @Valid BankCreateForm): ResponseDTO<String?> {
        return bankService.createBank(createVO)
    }

    /** 编辑银行信息 @author 善逸 */
    @PostMapping("/oa/bank/update")
    fun updateBank(@RequestBody updateVO: @Valid BankUpdateForm): ResponseDTO<String?> {
        return bankService.updateBank(updateVO)
    }

    /** 删除银行信息 @author 善逸 */
    @GetMapping("/oa/bank/delete/{bankId}")
    fun deleteBank(@PathVariable bankId: String?): ResponseDTO<String?> {
        return bankService.deleteBank(bankId)
    }
}
