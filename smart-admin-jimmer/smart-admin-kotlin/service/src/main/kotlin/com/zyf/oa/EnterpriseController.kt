package com.zyf.oa

import cn.hutool.core.date.DateUtil
import com.zyf.common.annotations.Body
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.RequestUser
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.utils.SmartRequestUtil
import com.zyf.login.domain.RequestEmployee
import com.zyf.oa.service.EnterpriseService
import com.zyf.runtime.utils.SmartExcelUtil
import com.zyf.runtime.utils.SmartResponseUtil
import com.zyf.service.dto.*
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*

@Api("Enterprise Api")
@RestController
class EnterpriseController(
    val sql: KSqlClient,
    val enterpriseService: EnterpriseService
) {

    /** 分页查询企业模块 @author 开云 */
    @PostMapping("/oa/enterprise/page/query")
    fun queryByPage(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: EnterpriseQueryForm
    ): ResponseDTO<PageResult<EnterpriseVO>> {
        return enterpriseService.queryByPage(pageBean, queryForm)
    }

    /** 导出企业信息 @author 卓大 */
    @PostMapping("/oa/enterprise/exportExcel")
    fun exportExcel(@RequestBody @Valid queryForm: EnterpriseQueryForm, response: HttpServletResponse) {
        val data = enterpriseService.getExcelExportData(queryForm)
        if (data.isEmpty()) {
            SmartResponseUtil.write(response, ResponseDTO.userErrorParam<String>("暂无数据"))
            return
        }
        val watermark = "admin " + DateUtil.now()

        SmartExcelUtil.exportExcelWithWatermark(
            response,
            "企业基本信息.xlsx",
            "企业信息",
            EnterpriseExcelVO::class.java,
            data,
            watermark
        )
    }

    /** 查询企业详情 @author 开云 */
    @GetMapping("/oa/enterprise/get/{enterpriseId}")
    fun getDetail(@PathVariable enterpriseId: String): ResponseDTO<EnterpriseDetailVO?> {
        return ResponseDTO.ok(enterpriseService.getDetail(enterpriseId))
    }

    /** 新建企业 @author 开云 */
    @PostMapping("/oa/enterprise/create")
    fun createEnterprise(@RequestBody @Valid createVO: EnterpriseCreateForm): ResponseDTO<String?> {
        return enterpriseService.createEnterprise(createVO)
    }

    /** 编辑企业 @author 开云 */
    @PostMapping("/oa/enterprise/update")
    fun updateEnterprise(@RequestBody @Valid updateVO: EnterpriseUpdateForm): ResponseDTO<String?> {
        return enterpriseService.updateEnterprise(updateVO)
    }

    /** 删除企业 @author 开云 */
    @GetMapping("/oa/enterprise/delete/{enterpriseId}")
    fun deleteEnterprise(@PathVariable enterpriseId: String): ResponseDTO<String?> {
        return enterpriseService.deleteEnterprise(enterpriseId)
    }

    /** 按照类型查询企业 @author 开云 */
    @GetMapping("/oa/enterprise/query/list")
    fun queryList(@RequestParam(value = "type", required = false) type: Int?): ResponseDTO<List<EnterpriseListVO>> {
        return enterpriseService.queryList(type)
    }

    /** 企业添加员工 @author 罗伊 */
    @PostMapping("/oa/enterprise/employee/add")
    fun addEmployee(@RequestBody @Valid enterpriseEmployeeForm: EnterpriseEmployeeForm): ResponseDTO<String?> {
        return enterpriseService.addEmployee(enterpriseEmployeeForm)
    }

    /** 查询企业全部员工 @author 罗伊 */
    @PostMapping("/oa/enterprise/employee/list")
    fun employeeList(@RequestBody @Valid enterpriseIdList: List<String>): ResponseDTO<List<EnterpriseEmployeeVO>> {
        return ResponseDTO.ok(enterpriseService.employeeList(enterpriseIdList))
    }

    /** 分页查询企业员工 @author 卓大 */
    @PostMapping("/oa/enterprise/employee/queryPage")
    fun queryPageEmployeeList(
        @Body pageBean: PageBean,
        @RequestBody @Valid queryForm: EnterpriseEmployeeQueryForm
    ): ResponseDTO<PageResult<EnterpriseEmployeeVO>> {
        return ResponseDTO.ok(enterpriseService.queryPageEmployeeList(pageBean, queryForm))
    }

    /** 企业删除员工 @author 罗伊 */
    @PostMapping("/oa/enterprise/employee/delete")
    fun deleteEmployee(@RequestBody @Valid enterpriseEmployeeForm: EnterpriseEmployeeForm): ResponseDTO<String?> {
        return enterpriseService.deleteEmployee(enterpriseEmployeeForm)
    }
}