package com.zyf.system

import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.RoleDataScopeUpdateForm
import com.zyf.service.dto.RoleDataScopeVO
import com.zyf.system.domain.DataScopeAndViewTypeVO
import com.zyf.system.service.RoleDataScopeService
import jakarta.validation.Valid
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.*

@Api("RoleDataScope Api")
@RestController
class RoleDataScopeController(
    val sqlClient: KSqlClient,
    val roleDataScopeService: RoleDataScopeService
) {
    /** 获取当前系统所配置的所有数据范围 @author 罗伊 */
    @GetMapping("/dataScope/list")
    fun dataScopeList(): ResponseDTO<List<DataScopeAndViewTypeVO>> {
        return roleDataScopeService.dataScopeList()
    }

    /** 获取某角色所设置的数据范围 @author 卓大 */
    @GetMapping("/role/dataScope/getRoleDataScopeList/{roleId}")
    fun dataScopeListByRole(@PathVariable roleId: String): ResponseDTO<List<RoleDataScopeVO>> {
        return roleDataScopeService.getRoleDataScopeList(roleId)
    }

    /** 批量设置某角色数据范围 @author 卓大 */
    @PostMapping("/role/dataScope/updateRoleDataScopeList")
    fun updateRoleDataScopeList(@RequestBody @Valid roleDataScopeUpdateForm: RoleDataScopeUpdateForm): ResponseDTO<String?> {
        return roleDataScopeService.updateRoleDataScopeList(roleDataScopeUpdateForm)
    }

}