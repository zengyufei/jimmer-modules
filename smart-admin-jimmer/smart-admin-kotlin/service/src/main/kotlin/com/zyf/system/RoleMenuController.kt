package com.zyf.system

import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.RoleMenuUpdateForm
import com.zyf.system.domain.RoleMenuTreeVO
import com.zyf.system.service.RoleMenuService
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*

@Api("RoleMenu Api")
@RestController
class RoleMenuController(
    val sqlClient: KSqlClient,
    val roleMenuService: RoleMenuService
) {
    /** 更新角色权限 @author 卓大 */
    @PostMapping("/role/menu/updateRoleMenu")
    fun updateRoleMenu(@Valid @RequestBody updateDTO: RoleMenuUpdateForm): ResponseDTO<String?> {
        return roleMenuService.updateRoleMenu(updateDTO)
    }

    /** 获取角色关联菜单权限 @author 卓大 */
    @GetMapping("/role/menu/getRoleSelectedMenu/{roleId}")
    fun getRoleSelectedMenu(@PathVariable roleId: String): ResponseDTO<RoleMenuTreeVO> {
        return roleMenuService.getRoleSelectedMenu(roleId)
    }

}