package com.zyf.department.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.department.domain.DepartmentTreeVO
import com.zyf.employee.*
import com.zyf.repository.employee.DepartmentRepository
import com.zyf.service.dto.DepartmentAddForm
import com.zyf.service.dto.DepartmentUpdateForm
import com.zyf.service.dto.DepartmentVO
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.count
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Slf4j
@Service
class DepartmentService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val departmentRepository: DepartmentRepository,
) {

    fun <T : View<Department>> list(viewType: KClass<T>): MutableList<T> {
        return sql.createQuery(Department::class) {
            orderBy(table.sort.asc())
            select(
                table.fetch(viewType)
            )
        }.execute().toMutableList()
    }

    fun listAll(): MutableList<DepartmentVO> {
        return list(DepartmentVO::class)
    }

    fun departmentTree(): MutableList<DepartmentTreeVO> {
        val list = list(DepartmentVO::class)
        return buildDepartmentTree(list)
    }

    fun queryPage(pageBean: PageBean): PageResult<Department> {
        return sql.createQuery(Department::class) {
            orderBy(pageBean)
            select(
                table
            )
        }.page(pageBean)
    }

    fun addDepartment(createDTO: DepartmentAddForm): Department {
        val insert = sql.insert(createDTO)
        clearCache()
        return insert.originalEntity
    }

    fun updateDepartment(updateDTO: DepartmentUpdateForm): Department {
//        var department = updateDTO.toEntity()
//        if (updateDTO.departmentId == "1") {
//            department = updateDTO.toEntity {
//                parentId = null
//            }
//        }

        val update = sql.update(updateDTO)
        clearCache()
        return update.originalEntity
    }


    fun deleteDepartment(departmentId: String): String? {
//        if (departmentId == "1") {
//            throw RuntimeException("根节点不允许删除")
//        }

        // 是否有子级部门
        val subDepartmentNum: Long = sql.createQuery(Department::class) {
            where(table.parentId eq departmentId)
            select(count(table))
        }.fetchOne()
        if (subDepartmentNum > 0) {
            throw RuntimeException("请先删除子级部门")
        }


        // 是否有未删除员工
        val employeeNum: Long = sql.createQuery(Employee::class) {
            where(table.departmentId eq departmentId)
            where(table.disabledFlag eq false)
            select(count(table))
        }.fetchOne()
        if (employeeNum > 0) {
            throw RuntimeException("请先删除部门员工")
        }


        sql.deleteById(Department::class, departmentId)

        // 清除缓存
        this.clearCache()
        return null
    }

    /**
     * 清除自身以及下级的id列表缓存
     */
    private fun clearCache() {
    }


    /**
     * 构建部门树结构
     */
    fun buildDepartmentTree(departments: MutableList<DepartmentVO>): MutableList<DepartmentTreeVO> {
        if (departments.isEmpty()) return mutableListOf()

        // 转换所有部门为树节点
        val departmentMap = departments.associate {
            it.departmentId to DepartmentTreeVO(it.toEntity())
        }

        // 构建树结构
        val roots = mutableListOf<DepartmentTreeVO>()
        departmentMap.values.forEach { dept ->
            val parentId = dept.parentId
            if (parentId == null) {
                // 根节点
                roots.add(dept)
            } else {
                // 将当前节点添加到父节点的children中
                departmentMap[parentId]?.let { parent ->
                    if (parent.children == null) {
                        parent.children = mutableListOf()
                    }
                    parent.children?.add(dept)
                }
            }
        }

        // 处理每个节点的兄弟关系和子节点ID集合
        processDepartmentNodes(roots)

        return roots
    }

    /**
     * 处理节点的兄弟关系和子节点ID集合
     */
    private fun processDepartmentNodes(nodes: MutableList<DepartmentTreeVO>) {
        // 处理同级节点的前后关系
        nodes.forEachIndexed { index, node ->
            node.preId = if (index > 0) nodes[index - 1].departmentId else null
            node.nextId = if (index < nodes.size - 1) nodes[index + 1].departmentId else null

            // 初始化并填充子节点ID集合
            node.selfAndAllChildrenIdList = mutableListOf(node.departmentId)

            // 递归处理子节点
            node.children?.let { children ->
                processDepartmentNodes(children)
                // 将所有子节点的ID添加到当前节点的集合中
                children.forEach { child ->
                    child.selfAndAllChildrenIdList?.let {
                        node.selfAndAllChildrenIdList?.addAll(it)
                    }
                }
            }
        }
    }


    /**
     * 通过部门id,获取当前以及下属部门
     *
     */
    fun selfAndChildrenIdList(departmentId: String): List<String> {
        val voList = departmentRepository.listAll()
        val selfAndChildrenIdList: MutableList<String> = mutableListOf()
        if (voList.isEmpty()) {
            return selfAndChildrenIdList
        }
        selfAndChildrenIdList.add(departmentId)
        val children: List<DepartmentTreeVO> = this.getChildren(departmentId, voList)
        if (children.isEmpty()) {
            return selfAndChildrenIdList
        }
        val childrenIdList: List<String> = children.map { it.departmentId }
        selfAndChildrenIdList.addAll(childrenIdList)
        for (childId in childrenIdList) {
            this.selfAndChildrenRecursion(selfAndChildrenIdList, childId, voList);
        }
        return selfAndChildrenIdList;
    }


    /**
     * 获取子元素
     *
     */
    fun getChildren(departmentId: String, voList: List<Department>): List<DepartmentTreeVO> {
        val childrenEntityList: List<Department> = voList.filter { departmentId == it.parentId }
        if (childrenEntityList.isEmpty()) {
            return mutableListOf();
        }
        val newList: MutableList<DepartmentTreeVO> = mutableListOf()
        for (department in childrenEntityList) {
            val vo = DepartmentTreeVO(department)
            newList.add(vo)
        }
        return newList
    }

    /**
     * 递归查询
     */
    fun selfAndChildrenRecursion(selfAndChildrenIdList: MutableList<String>, departmentId: String, voList: List<Department>) {
        val children: List<DepartmentTreeVO> = this.getChildren(departmentId, voList);
        if (children.isEmpty()) {
            return;
        }
        val childrenIdList: List<String> = children.map { it.departmentId }
        selfAndChildrenIdList.addAll(childrenIdList);
        for (childId in childrenIdList) {
            this.selfAndChildrenRecursion(selfAndChildrenIdList, childId, voList);
        }
    }

//    private fun handlerParentIds(list: MutableList<DepartmentTreeVO>): MutableList<DepartmentTreeVO> {
//        // 按 parentId 分组
//        val parentIdGroupByBeanMap = list.groupBy { it.parentId }
//
//        // 递归处理节点
//        fun processNode(node: DepartmentTreeVO): DepartmentTreeVO {
//            val departmentId = node.departmentId
//            val parentId = node.parentId
//
//            // 收集所有父节点的 departmentId
//            val selfAndAllChildrenIdList = generateSequence(node.parent) { it.parent }
//                .map { it.departmentId }
//                .toMutableList()
//
//            // 获取兄弟节点
//            val siblings = parentIdGroupByBeanMap[parentId].orEmpty()
//            val idx = siblings.indexOfFirst { it.departmentId == departmentId }
//            val newPreId = if (idx > 0) siblings[idx - 1].departmentId else null
//            val newNextId = if (idx < siblings.size - 1) siblings[idx + 1].departmentId else null
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
//        val idByBeanMap = newList.associateBy { it.departmentId }
//
//        val nnewList = newList.map { node ->
//            // 创建新的节点
//            val nodeChildren = node.children
//            node.copy(
//                children = nodeChildren?.map { child ->
//                    val ch = idByBeanMap[child.departmentId]
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
//        ch: DepartmentTreeVO,
//        idByBeanMap: Map<String, DepartmentTreeVO>
//    ):List<DepartmentTreeVO.TargetOf_children>? {
//        return ch.children?.map { cchild ->
//            val cch = idByBeanMap[cchild.departmentId]
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