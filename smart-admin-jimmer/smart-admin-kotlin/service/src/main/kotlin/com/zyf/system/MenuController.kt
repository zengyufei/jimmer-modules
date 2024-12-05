package com.zyf.system

import com.zyf.common.domain.ResponseDTO
import com.zyf.service.dto.MenuTreeVO
import com.zyf.service.dto.MenuVO
import com.zyf.system.service.MenuService
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Api("menu Api")
@RestController
class MenuController(
    val sqlClient: KSqlClient,
    val menuService: MenuService,
) {

    /** 查询菜单列表 */
    @Api
    @GetMapping("/menu/query")
    fun listAll(): ResponseDTO<MutableList<MenuVO>> {
        return ResponseDTO.ok(menuService.listAll(null, null))
    }


    /** 查询菜单树 */
    @GetMapping("/menu/tree")
    fun menuTree(@RequestParam("onlyMenu")  onlyMenu: Boolean?): ResponseDTO<MutableList<MenuTreeVO>> {
        return ResponseDTO.ok(menuService.menuTree(onlyMenu))
    }

//    /** 分页查询 */
//    @Api
//    @PostMapping("/menu/queryPage")
//    fun queryPage(
//        @RequestParam(defaultValue = "0") pageNum: Int,
//        @RequestParam(defaultValue = "5") pageSize: Int,
//        @RequestParam(defaultValue = "sort asc") sortCode: String
//    ): ResponseDTO<PageResult<Menu>> {
//        return ResponseDTO.ok(
//            menuService.queryPage(
//               PageBean.of(pageNum, pageSize, sortCode)
//            )
//        )
//    }
//
//
//    /** 添加部门 */
//    @PostMapping("/menu/add")
////    @SaCheckPermission("system:menu:add")
//    fun addMenu(@RequestBody @Valid  createDTO: MenuAddForm): ResponseDTO<Menu> {
//        return ResponseDTO.ok(menuService.addMenu(createDTO))
//    }
//
//    /** 更新部门 */
//    @PostMapping("/menu/update")
////    @SaCheckPermission("system:menu:update")
//    fun updateMenu(@RequestBody @Valid  updateDTO: MenuUpdateForm): ResponseDTO<Menu> {
//        return ResponseDTO.ok(menuService.updateMenu(updateDTO))
//    }
//
//
//    /** 删除部门 */
//    @GetMapping("/menu/delete/{menuId}")
////    @SaCheckPermission("system:menu:delete")
//    fun deleteMenu(@PathVariable menuId: String): ResponseDTO<String?> {
//        return ResponseDTO.ok(menuService.deleteMenu(menuId))
//    }
}