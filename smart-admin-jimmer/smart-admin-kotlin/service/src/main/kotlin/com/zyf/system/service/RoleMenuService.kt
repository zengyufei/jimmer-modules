package com.zyf.system.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.MenuSimpleTreeVO
import com.zyf.service.dto.MenuVO
import com.zyf.service.dto.RoleMenuUpdateForm
import com.zyf.system.Menu
import com.zyf.system.domain.RoleMenuTreeVO
import com.zyf.system.menuId
import com.zyf.system.roleId
import com.zyf.system.roles
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import org.springframework.stereotype.Service

@Slf4j
@Service
class RoleMenuService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val menuService: MenuService,
) {

    /**
     * 更新角色权限
     */
    fun updateRoleMenu(roleMenuUpdateForm: RoleMenuUpdateForm): ResponseDTO<String?> {
//        // 查询角色是否存在
//        val roleId = roleMenuUpdateForm.roleId
//        sql.findById(Role::class, roleId!!) ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)

        sql.update(roleMenuUpdateForm)
        return ResponseDTO.ok()
    }

    /**
     * 根据角色id集合，查询其所有的菜单权限
     */
    fun getMenuList(roleIdList: List<String>, administratorFlag: Boolean?): List<MenuVO> {
        // 管理员返回所有菜单
        administratorFlag?.let {
            if (administratorFlag) {
                return sql.findAll(MenuVO::class)
            }
        }
        // 非管理员 无角色 返回空菜单
        if (roleIdList.isEmpty()) {
            return emptyList()
        }
        val menuVOS = sql.findAll(MenuVO::class) {
            roleIdList.takeIf { it.isNotEmpty() } ?.let {
                where += table.roles {
                    roleId valueIn roleIdList
                }
            }
        }
        return menuVOS
    }

    /**
     * 获取角色关联菜单权限
     */
    fun getRoleSelectedMenu(inputRoleId: String): ResponseDTO<RoleMenuTreeVO> {
        // 查询角色ID选择的菜单权限
        val menuIds = sql.createQuery(Menu::class) {
            where += table.roles {
                roleId eq inputRoleId
            }
            select(table.menuId)
        }.execute()
        val res = RoleMenuTreeVO().apply {
            this.roleId = inputRoleId
            selectedMenuId = menuIds
            // 查询菜单权限
            val menuVOList = menuService.list(null, null, MenuSimpleTreeVO::class, true)
            menuTreeList = menuVOList
        }
        return ResponseDTO.ok(res)
    }

}