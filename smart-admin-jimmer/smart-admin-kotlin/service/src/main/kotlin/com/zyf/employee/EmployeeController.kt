package com.zyf.employee

import com.zyf.common.annotations.Body
import com.zyf.common.annotations.Operation
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.employee.service.EmployeeService
import com.zyf.login.domain.RequestEmployee
import com.zyf.service.dto.*
import com.zyf.support.service.Level3ProtectConfigService
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*

@Api("Employee Api")
@RestController
class EmployeeController(
    val sqlClient: KSqlClient,
    val employeeService: EmployeeService,
    val level3ProtectConfigService: Level3ProtectConfigService,
) {

    /** 员工管理查询 @author 卓大 */
    @Operation(summary = "员工管理查询 @author 卓大")
    @PostMapping("/employee/query")
    fun query(
        @Body pageBean: PageBean,
        @RequestBody query: EmployeeQueryForm
    ): ResponseDTO<PageResult<EmployeeVO>> {
        return employeeService.queryEmployee(pageBean, query)
    }

    /** 添加员工(返回添加员工的密码) @author 卓大 */
    @Operation(summary = "添加员工(返回添加员工的密码) @author 卓大")
    @PostMapping("/employee/add")
    fun addEmployee(@RequestBody employeeAddForm: EmployeeAddForm): ResponseDTO<String?> {
        return employeeService.addEmployee(employeeAddForm)
    }

    /** 更新员工 @author 卓大 */
    @Operation(summary = "更新员工 @author 卓大")
    @PostMapping("/employee/update")
    fun updateEmployee(@RequestBody employeeUpdateForm: EmployeeUpdateForm): ResponseDTO<String?> {
        return employeeService.updateEmployee(employeeUpdateForm)
    }

    /** 更新登录人信息 @author 善逸 */
    @Operation(summary = "更新登录人信息 @author 善逸")
    @PostMapping("/employee/update/login")
    fun updateByLogin(@RequestBody employeeUpdateForm: EmployeeUpdateForm): ResponseDTO<String?> {
        val requestUser = RequestEmployee()
        requestUser.userId = "1"
        val newForm = employeeUpdateForm.copy(
            employeeId = requestUser.userId
        )
        return employeeService.updateEmployee(newForm)
    }

    /** 更新登录人头像 @author 善逸 */
    @Operation(summary = "更新登录人头像 @author 善逸")
    @PostMapping("/employee/update/avatar")
    fun updateAvatar(@RequestBody employeeUpdateAvatarForm: EmployeeUpdateAvatarForm): ResponseDTO<String?> {
        val requestUser = RequestEmployee()
        requestUser.userId = "1"
        val newForm = employeeUpdateAvatarForm.copy(
            employeeId = requestUser.userId
        )
        return employeeService.updateAvatar(newForm)
    }

    /** 更新员工禁用/启用状态 @author 卓大 */
    @Operation(summary = "更新员工禁用/启用状态 @author 卓大")
    @GetMapping("/employee/update/disabled/{employeeId}")
    fun updateDisableFlag(@PathVariable employeeId: String): ResponseDTO<String?> {
        return employeeService.updateDisableFlag(employeeId)
    }

    /** 批量删除员工 @author 卓大 */
    @Operation(summary = "批量删除员工 @author 卓大")
    @PostMapping("/employee/update/batch/delete")
    fun batchUpdateDeleteFlag(@RequestBody employeeIdList: List<String>): ResponseDTO<String?> {
        return employeeService.batchUpdateDeleteFlag(employeeIdList)
    }

    /** 批量调整员工部门 @author 卓大 */
    @Operation(summary = "批量调整员工部门 @author 卓大")
    @PostMapping("/employee/update/batch/department")
    fun batchUpdateDepartment(@RequestBody batchUpdateDepartmentForm: EmployeeBatchUpdateDepartmentForm): ResponseDTO<String?> {
        return employeeService.batchUpdateDepartment(batchUpdateDepartmentForm)
    }

    /** 修改密码 @author 卓大 */
    @Operation(summary = "修改密码 @author 卓大")
    @PostMapping("/employee/update/password")
//    @ApiDecrypt
    fun updatePassword(@RequestBody updatePasswordForm: EmployeeUpdatePasswordForm): ResponseDTO<String?> {
        val requestUser = RequestEmployee()
        requestUser.userId = "1"
        val newForm = updatePasswordForm.copy(
            employeeId = requestUser.userId
        )
        return employeeService.updatePassword(requestUser, newForm)
    }

    /** 获取密码复杂度 @author 卓大 */
    @Operation(summary = "获取密码复杂度 @author 卓大")
    @GetMapping("/employee/getPasswordComplexityEnabled")
//    @ApiDecrypt
    fun getPasswordComplexityEnabled(): ResponseDTO<Boolean> {
        return ResponseDTO.ok(level3ProtectConfigService.isPasswordComplexityEnabled)
    }

    /** 重置员工密码 @author 卓大 */
    @Operation(summary = "重置员工密码 @author 卓大")
    @GetMapping("/employee/update/password/reset/{employeeId}")
    fun resetPassword(@PathVariable employeeId: String): ResponseDTO<String?> {
        return employeeService.resetPassword(employeeId)
    }

    /** 查询员工-根据部门id @author 卓大 */
    @Operation(summary = "查询员工-根据部门id @author 卓大")
    @GetMapping("/employee/getAllEmployeeByDepartmentId/{departmentId}")
    fun getAllEmployeeByDepartmentId(@PathVariable departmentId: String): ResponseDTO<List<EmployeeVO>> {
        return employeeService.getAllEmployeeByDepartmentId(departmentId, false)
    }

    /** 查询所有员工 @author 卓大 */
    @Operation(summary = "查询所有员工 @author 卓大")
    @GetMapping("/employee/queryAll")
    fun queryAllEmployee(@RequestParam(value = "disabledFlag", required = false) disabledFlag: Boolean?): ResponseDTO<List<EmployeeVO>> {
        return employeeService.queryAllEmployee(disabledFlag)
    }

}