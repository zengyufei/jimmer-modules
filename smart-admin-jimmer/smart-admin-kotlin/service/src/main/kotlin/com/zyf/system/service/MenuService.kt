package com.zyf.system.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.code.SystemErrorCode
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.MenuTypeEnum
import com.zyf.common.utils.SmartBeanUtil
import com.zyf.service.dto.MenuAddForm
import com.zyf.service.dto.MenuTreeVO
import com.zyf.service.dto.MenuUpdateForm
import com.zyf.service.dto.MenuVO
import com.zyf.system.*
import com.zyf.system.domain.RequestUrlVO
import jakarta.annotation.Resource
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.springframework.stereotype.Service
import java.awt.SystemColor.menu
import kotlin.reflect.KClass

/**
 * 菜单
 *
 * @Author 1024创新实验室: 善逸
 * @Date 2022-03-08 22:15:09
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
@Service
class MenuService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val authUrl: List<RequestUrlVO>,
) {

    fun <T : View<Menu>> list(
        disabledFlag: Boolean?,
        menuTypes: List<Int>?,
        viewType: KClass<T>,
        isRoot: Boolean = false
    ): MutableList<T> {
        return sql.createQuery(Menu::class) {
            orderBy(table.parentId.desc(), table.sort.asc())
            where(table.disabledFlag `eq?` disabledFlag)
            menuTypes?.takeIf { it.isNotEmpty() }?.let {
                where(table.menuType `valueIn?` menuTypes)
            }
            if (isRoot) {
                where(table.parentId eq null)
            }
            select(
                table.fetch(viewType)
            )
        }.execute().toMutableList()
    }

    fun listAll(disabledFlag: Boolean?, menuTypes: List<Int>?): MutableList<MenuVO> {
        return list(disabledFlag, menuTypes, MenuVO::class)
    }

    fun queryMenuTree(onlyMenu: Boolean?): ResponseDTO<MutableList<MenuTreeVO>> {
        val menuTypes = mutableListOf<Int>()
        if (onlyMenu == true) {
            menuTypes.add(MenuTypeEnum.CATALOG.value)
            menuTypes.add(MenuTypeEnum.MENU.value)
        }
        val toMutableList = list(null, menuTypes, MenuTreeVO::class, true)
        return ResponseDTO.ok(toMutableList)
    }

    /**
     * 添加菜单
     */
    @Synchronized
    fun addMenu(menuAddForm: MenuAddForm): ResponseDTO<String?> {
        // 校验菜单名称
        if (sql.createQuery(Menu::class) {
                where(table.menuName eq menuAddForm.menuName)
                where(table.parentId eq menuAddForm.parentId)
                select(table)
            }.exists()) {
            return ResponseDTO.userErrorParam("菜单名称已存在")
        }
        // 校验前端权限字符串
        if (sql.createQuery(Menu::class) {
                where(table.webPerms eq menuAddForm.webPerms)
                select(table)
            }.exists()) {
            return ResponseDTO.userErrorParam("前端权限字符串已存在")
        }
        sql.insert(menuAddForm)
        return ResponseDTO.ok()
    }

    /**
     * 更新菜单
     */
    @Synchronized
    fun updateMenu(menuUpdateForm: MenuUpdateForm): ResponseDTO<String?> {
        // 校验菜单是否存在
        sql.findById(Menu::class, menuUpdateForm.menuId) ?: return ResponseDTO.userErrorParam("菜单不存在")
        // 校验菜单名称
        if (sql.createQuery(Menu::class) {
                where(table.menuName eq menuUpdateForm.menuName)
                where(table.parentId eq menuUpdateForm.parentId)
                where(table.menuId ne menuUpdateForm.menuId)
                select(table)
            }.exists()) {
            return ResponseDTO.userErrorParam("菜单名称已存在")
        }
        // 校验前端权限字符串
        if (menuUpdateForm.webPerms !=null && sql.createQuery(Menu::class) {
                where(table.webPerms eq menuUpdateForm.webPerms)
                where(table.menuId ne menuUpdateForm.menuId)
                select(table)
            }.exists()) {
            return ResponseDTO.userErrorParam("前端权限字符串已存在")
        }
        if (menuUpdateForm.menuId == menuUpdateForm.parentId) {
            return ResponseDTO.userErrorParam("上级菜单不能为自己")
        }
        sql.update(menuUpdateForm)
        return ResponseDTO.ok()
    }

    /**
     * 批量删除菜单
     */
    @Synchronized
    fun batchDeleteMenu(menuIdList: List<String>): ResponseDTO<String?> {
        if (menuIdList.isEmpty()) {
            return ResponseDTO.userErrorParam("所选菜单不能为空")
        }
        sql.deleteByIds(Menu::class, menuIdList)
        // 孩子节点也需要删除
//        recursiveDeleteChildren(menuIdList, employeeId)
        return ResponseDTO.ok()
    }

//    private fun recursiveDeleteChildren(menuIdList: List<String>, employeeId: String) {
//        val childrenMenuIdList = menuDao.selectMenuIdByParentIdList(menuIdList)
//        if (CollectionUtil.isEmpty(childrenMenuIdList)) {
//            return
//        }
//        menuDao.deleteByMenuIdList(childrenMenuIdList, employeeId, true)
//        recursiveDeleteChildren(childrenMenuIdList, employeeId)
//    }


    /**
     * 查询菜单详情
     */
    fun getMenuDetail(menuId: String): ResponseDTO<MenuVO?> {
        // 校验菜单是否存在
        val selectMenu = sql.findById(Menu::class, menuId) ?: return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "菜单不存在")
        val menuVO = SmartBeanUtil.copy(selectMenu, MenuVO::class.java)
        return ResponseDTO.ok(menuVO)
    }

    /**
     * 获取系统所有请求路径
     */
    fun getAuthUrl(): ResponseDTO<List<RequestUrlVO>> {
        return ResponseDTO.ok(authUrl)
    }
}

