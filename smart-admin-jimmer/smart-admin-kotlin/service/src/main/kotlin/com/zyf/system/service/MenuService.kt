package com.zyf.system.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.enums.MenuTypeEnum
import com.zyf.service.dto.MenuTreeVO
import com.zyf.service.dto.MenuVO
import com.zyf.system.*
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Slf4j
@Service
class MenuService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
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
            menuTypes?.takeIf { it.isNotEmpty() } ?.let {
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

    fun menuTree(onlyMenu: Boolean?): MutableList<MenuTreeVO> {
        val menuTypes = mutableListOf<Int>()
        if (onlyMenu == true) {
            menuTypes.add(MenuTypeEnum.CATALOG.value)
            menuTypes.add(MenuTypeEnum.MENU.value)
        }
        val toMutableList = list(null, menuTypes, MenuTreeVO::class, true)
        return toMutableList
    }

//    fun queryPage(pageBean: PageBean): PageResult<Menu> {
//        return sql.createQuery(Menu::class) {
//            orderBy(pageBean)
//            select(
//                table
//            )
//        }.page(pageBean)
//    }
//
//    fun addMenu(createDTO: MenuAddForm): Menu {
//        val insert = sql.insert(createDTO)
//        clearCache()
//        return insert.originalEntity
//    }
//
//    fun updateMenu(updateDTO: MenuUpdateForm): Menu {
////        var menu = updateDTO.toEntity()
////        if (updateDTO.menuId == "1") {
////            menu = updateDTO.toEntity {
////                parentId = null
////            }
////        }
//
//        val update = sql.update(updateDTO)
//        clearCache()
//        return update.originalEntity
//    }
//
//
//    fun deleteMenu(menuId: String): String? {
////        if (menuId == "1") {
////            throw RuntimeException("根节点不允许删除")
////        }
//
//        // 是否有子级部门
//        val subMenuNum: Long = sql.createQuery(Menu::class) {
//            where(table.parentId eq menuId)
//            select(count(table))
//        }.fetchOne()
//        if (subMenuNum > 0) {
//            throw RuntimeException("请先删除子级部门")
//        }
//
//
//        // 是否有未删除员工
//        val employeeNum: Long = sql.createQuery(Employee::class) {
//            where(table.menuId eq menuId)
//            where(table.disabledFlag eq 0)
//            select(count(table))
//        }.fetchOne()
//        if (employeeNum > 0) {
//            throw RuntimeException("请先删除部门员工")
//        }
//
//
//        sql.deleteById(Menu::class, menuId)
//
//        // 清除缓存
//        this.clearCache()
//        return null
//    }
//
//    /**
//     * 清除自身以及下级的id列表缓存
//     */
//    private fun clearCache() {
//    }
//    /**
//     * 构建部门树结构
//     */
//    fun buildMenuTree(menus: MutableList<MenuVO>): MutableList<MenuTreeVO> {
//        if (menus.isEmpty()) return mutableListOf()
//
//        // 转换所有部门为树节点
//        val menuMap = menus.associate {
//            it.menuId to MenuTreeVO(it.toEntity())
//        }
//
//        // 构建树结构
//        val roots = mutableListOf<MenuTreeVO>()
//        menuMap.values.forEach { dept ->
//            val parentId = dept.parentId
//            if (parentId == null) {
//                // 根节点
//                roots.add(dept)
//            } else {
//                // 将当前节点添加到父节点的children中
//                menuMap[parentId]?.let { parent ->
//                    if (parent.children == null) {
//                        parent.children = mutableListOf()
//                    }
//                    parent.children?.add(dept)
//                }
//            }
//        }
//
//        // 处理每个节点的兄弟关系和子节点ID集合
//        processMenuNodes(roots)
//
//        return roots
//    }
//
//    /**
//     * 处理节点的兄弟关系和子节点ID集合
//     */
//    private fun processMenuNodes(nodes: MutableList<MenuTreeVO>) {
//        // 处理同级节点的前后关系
//        nodes.forEachIndexed { index, node ->
//            node.preId = if (index > 0) nodes[index - 1].menuId else null
//            node.nextId = if (index < nodes.size - 1) nodes[index + 1].menuId else null
//
//            // 初始化并填充子节点ID集合
//            node.selfAndAllChildrenIdList = mutableListOf(node.menuId)
//
//            // 递归处理子节点
//            node.children?.let { children ->
//                processMenuNodes(children)
//                // 将所有子节点的ID添加到当前节点的集合中
//                children.forEach { child ->
//                    child.selfAndAllChildrenIdList?.let {
//                        node.selfAndAllChildrenIdList?.addAll(it)
//                    }
//                }
//            }
//        }
//    }


//    private fun handlerParentIds(list: MutableList<MenuTreeVO>): MutableList<MenuTreeVO> {
//        // 按 parentId 分组
//        val parentIdGroupByBeanMap = list.groupBy { it.parentId }
//
//        // 递归处理节点
//        fun processNode(node: MenuTreeVO): MenuTreeVO {
//            val menuId = node.menuId
//            val parentId = node.parentId
//
//            // 收集所有父节点的 menuId
//            val selfAndAllChildrenIdList = generateSequence(node.parent) { it.parent }
//                .map { it.menuId }
//                .toMutableList()
//
//            // 获取兄弟节点
//            val siblings = parentIdGroupByBeanMap[parentId].orEmpty()
//            val idx = siblings.indexOfFirst { it.menuId == menuId }
//            val newPreId = if (idx > 0) siblings[idx - 1].menuId else null
//            val newNextId = if (idx < siblings.size - 1) siblings[idx + 1].menuId else null
//
//            // 创建新的节点
//            val newNode = node.copy(
//                selfAndAllChildrenIdList = selfAndAllChildrenIdList,
//                preId = newPreId,
//                nextId = newNextId,
//            )
//            return newNode
//        }
//
//        // 处理每个节点并返回新的列表
//        val newList = list.map { processNode(it) }
//
//        val idByBeanMap = newList.associateBy { it.menuId }
//
//        val nnewList = newList.map { node ->
//            // 创建新的节点
//            val nodeChildren = node.children
//            node.copy(
//                children = nodeChildren?.map { child ->
//                    val ch = idByBeanMap[child.menuId]
//                    if (ch != null) {
//                        child.copy(
//                            selfAndAllChildrenIdList = ch.selfAndAllChildrenIdList,
//                            preId = ch.preId,
//                            nextId = ch.nextId,
//                            children = targetofChildren(ch, idByBeanMap)
//                        )
//                    } else {
//                        child
//                    }
//                }?.toList()
//            )
//        }
//
//        return nnewList.toMutableList()
//    }
//
//    private fun targetofChildren(
//        ch: MenuTreeVO,
//        idByBeanMap: Map<String, MenuTreeVO>
//    ):List<MenuTreeVO.TargetOf_children>? {
//        return ch.children?.map { cchild ->
//            val cch = idByBeanMap[cchild.menuId]
//            if (cch != null) {
//                cchild.copy(
//                    selfAndAllChildrenIdList = cch.selfAndAllChildrenIdList,
//                    preId = cch.preId,
//                    nextId = cch.nextId,
//                    children = targetofChildren(cch, idByBeanMap)
//                )
//            } else {
//                cchild
//            }
//        }?.toList()
//    }


}