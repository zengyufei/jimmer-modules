package com.zyf.system

import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.RoleAddForm
import com.zyf.service.dto.RoleUpdateForm
import com.zyf.service.dto.RoleVO
import com.zyf.system.service.RoleService
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*

@Api("Role Api")
@RestController
class RoleController(
    val sqlClient: KSqlClient,
    val roleService: RoleService
) {

    /** 添加角色 @author 卓大 */
    @PostMapping("/role/add")
    fun addRole(@Valid @RequestBody roleAddForm: RoleAddForm): ResponseDTO<String?> {
        return roleService.addRole(roleAddForm)
    }

    /** 删除角色 @author 卓大 */
    @GetMapping("/role/delete/{roleId}")
    fun deleteRole(@PathVariable roleId: String): ResponseDTO<String?> {
        return roleService.deleteRole(roleId)
    }

    /** 更新角色 @author 卓大 */
    @PostMapping("/role/update")
    fun updateRole(@Valid @RequestBody roleUpdateDTO: RoleUpdateForm): ResponseDTO<String?> {
        return roleService.updateRole(roleUpdateDTO)
    }

    /** 获取角色数据 @author 卓大 */
    @GetMapping("/role/get/{roleId}")
    fun getRole(@PathVariable("roleId") roleId: Long): ResponseDTO<RoleVO?> {
        return roleService.getRoleById(roleId)
    }

    /** 获取所有角色 @author 卓大 */
    @GetMapping("/role/getAll")
    fun getAllRole(): ResponseDTO<List<RoleVO>> {
        return roleService.getAllRole()
    }

}