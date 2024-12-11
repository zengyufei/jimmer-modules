package com.zyf.system

import cn.dev33.satoken.annotation.SaCheckPermission
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.AdminSwaggerTagConst
import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.MenuAddForm
import com.zyf.service.dto.MenuTreeVO
import com.zyf.service.dto.MenuUpdateForm
import com.zyf.service.dto.MenuVO
import com.zyf.system.domain.RequestUrlVO
import com.zyf.system.service.MenuService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

/**
 * 菜单
 *
 * @Author 1024创新实验室: 善逸
 * @Date 2022-03-06 22:04:37
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@RestController
@Tag(name = AdminSwaggerTagConst.System.SYSTEM_MENU)
class MenuController(
    val menuService: MenuService
) {

    @Operation(summary = "添加菜单 @author 卓大")
    @PostMapping("/menu/add")
    @SaCheckPermission("system:menu:add")
    fun addMenu(@RequestBody menuAddForm: @Valid MenuAddForm): ResponseDTO<String?> {
        return menuService.addMenu(menuAddForm)
    }

    @Operation(summary = "更新菜单 @author 卓大")
    @PostMapping("/menu/update")
    @SaCheckPermission("system:menu:update")
    fun updateMenu(@RequestBody menuUpdateForm: @Valid MenuUpdateForm): ResponseDTO<String?> {
        return menuService.updateMenu(menuUpdateForm)
    }

    @Operation(summary = "批量删除菜单 @author 卓大")
    @GetMapping("/menu/batchDelete")
    @SaCheckPermission("system:menu:batchDelete")
    fun batchDeleteMenu(@RequestParam("menuIdList") menuIdList: List<String>): ResponseDTO<String?> {
        return menuService.batchDeleteMenu(menuIdList)
    }

    @Operation(summary = "查询菜单列表 @author 卓大")
    @GetMapping("/menu/query")
    fun queryMenuList(): ResponseDTO<List<MenuVO>> {
        return ResponseDTO.ok(menuService.listAll(null, null))
    }

    @Operation(summary = "查询菜单详情 @author 卓大")
    @GetMapping("/menu/detail/{menuId}")
    fun getMenuDetail(@PathVariable menuId: String): ResponseDTO<MenuVO?> {
        return menuService.getMenuDetail(menuId)
    }

    @Operation(summary = "查询菜单树 @author 卓大")
    @GetMapping("/menu/tree")
    fun queryMenuTree(@RequestParam("onlyMenu", required = false) onlyMenu: Boolean?): ResponseDTO<MutableList<MenuTreeVO>> {
        return menuService.queryMenuTree(onlyMenu)
    }

    @get:GetMapping("/menu/auth/url")
    @get:Operation(summary = "获取所有请求路径 @author 卓大")
    val authUrl: ResponseDTO<List<RequestUrlVO>>
        get() = menuService.getAuthUrl()
}
