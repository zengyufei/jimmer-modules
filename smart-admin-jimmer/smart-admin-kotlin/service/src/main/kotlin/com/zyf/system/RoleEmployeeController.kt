package com.zyf.system

import com.zyf.common.annotations.Body
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.*
import com.zyf.system.service.RoleEmployeeService
import com.zyf.system.service.RoleService
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*

@Api("Role Api")
@RestController
class RoleEmployeeController(
    val sqlClient: KSqlClient,
    val roleEmployeeService: RoleEmployeeService
) {

    /** 查询某个角色下的员工列表  @author 卓大 */
    @PostMapping("/role/employee/queryEmployee")
    fun queryEmployee(
        @Body pageBean: PageBean,
        @Valid @RequestBody roleEmployeeQueryForm: RoleEmployeeQueryForm
    ): ResponseDTO<PageResult<EmployeeVO>> {
        return roleEmployeeService.queryEmployee(pageBean, roleEmployeeQueryForm)
    }

   /** 获取某个角色下的所有员工列表(无分页)  @author 卓大 */
   @GetMapping("/role/employee/getAllEmployeeByRoleId/{roleId}")
   fun listAllEmployeeRoleId(@PathVariable roleId: String): ResponseDTO<List<EmployeeVO>> {
       return ResponseDTO.ok(roleEmployeeService.getAllEmployeeByRoleId(roleId))
   }

   /** 从角色成员列表中移除员工 @author 卓大 */
   @GetMapping("/role/employee/removeEmployee")
   fun removeEmployee(employeeId: String, roleId: String): ResponseDTO<String?> {
       return roleEmployeeService.removeRoleEmployee(employeeId, roleId)
   }

   /** 从角色成员列表中批量移除员工 @author 卓大 */
   @PostMapping("/role/employee/batchRemoveRoleEmployee")
   fun batchRemoveEmployee(@Valid @RequestBody updateForm: RoleEmployeeUpdateForm): ResponseDTO<String?> {
       return roleEmployeeService.batchRemoveRoleEmployee(updateForm)
   }

   /** 角色成员列表中批量添加员工 @author 卓大 */
   @PostMapping("/role/employee/batchAddRoleEmployee")
   fun addEmployeeList(@Valid @RequestBody addForm: RoleEmployeeUpdateForm): ResponseDTO<String?> {
       return roleEmployeeService.batchAddRoleEmployee(addForm)
   }

   /** 获取员工所有选中的角色和所有角色 @author 卓大 */
   @GetMapping("/role/employee/getRoles/{employeeId}")
   fun getRoleByEmployeeId(@PathVariable employeeId: String): ResponseDTO<List<RoleSelectedVO>> {
       return ResponseDTO.ok(roleEmployeeService.getRoleInfoListByEmployeeId(employeeId))
   }
}