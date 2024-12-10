package com.zyf.system.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.employee.*
import com.zyf.repository.employee.EmployeeRepository
import com.zyf.repository.system.RoleRepository
import com.zyf.service.dto.*
import com.zyf.system.Role
import com.zyf.system.roleId
import org.babyfish.jimmer.sql.ast.tuple.Tuple2
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.`eq?`
import org.babyfish.jimmer.sql.kt.ast.expression.`ilike?`
import org.babyfish.jimmer.sql.kt.ast.expression.or
import org.springframework.stereotype.Service

@Slf4j
@Service
class RoleEmployeeService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    private val employeeRepository: EmployeeRepository,
    private val roleRepository: RoleRepository
) {


    fun queryEmployee(
        pageBean: PageBean,
        roleEmployeeQueryForm: RoleEmployeeQueryForm
    ): ResponseDTO<PageResult<EmployeeVO>> {
        val pageResult = sql.createQuery(Employee::class) {
            orderBy(pageBean)
            where += table.roles {
                roleId `eq?` roleEmployeeQueryForm.roleId
            }
            where(
                or(
                    table.actualName `ilike?` roleEmployeeQueryForm.keywords,
                    table.phone `ilike?` roleEmployeeQueryForm.keywords,
                    table.loginName `ilike?` roleEmployeeQueryForm.keywords,
                )
            )
            select(table.fetch(EmployeeVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    fun batchInsert(roleEmployeeList: List<RoleEmployeeEntity>) {
        if (roleEmployeeList.isNotEmpty()) {
            sql.getAssociations(Employee::roles)
                .saveAll(roleEmployeeList.map {
                    Tuple2(it.employeeId, it.roleId)
                })
        }
    }

    fun getAllEmployeeByRoleId(roleId: String): List<EmployeeVO> {
        return employeeRepository.byRoleId(roleId) {
            select(table.fetch(EmployeeVO::class))
        }
    }

    fun removeRoleEmployee(employeeId: String?, roleId: String?): ResponseDTO<String?> {
        if (employeeId == null || roleId == null) {
            return ResponseDTO.userErrorParam()
        }
        sql.getAssociations(Employee::roles)
            .delete(employeeId, roleId)
        return ResponseDTO.ok()
    }

    fun batchRemoveRoleEmployee(roleEmployeeUpdateForm: RoleEmployeeUpdateForm): ResponseDTO<String?> {
        sql.getAssociations(Employee::roles)
            .deleteAll(roleEmployeeUpdateForm.employeeIdList.map {
                Tuple2(it, roleEmployeeUpdateForm.roleId)
            })

        return ResponseDTO.ok()
    }

    fun batchAddRoleEmployee(roleEmployeeUpdateForm: RoleEmployeeUpdateForm): ResponseDTO<String?> {
        val roleId = roleEmployeeUpdateForm.roleId

        // 已选择的员工id列表
        val selectedEmployeeIdList = roleEmployeeUpdateForm.employeeIdList
        // 数据库里已有的员工id列表


        val dbEmployeeIdList = employeeRepository.byRoleId(roleId) {
            select(table.employeeId)
        }
        // 从已选择的员工id列表里 过滤数据库里不存在的 即需要添加的员工 id
        val addEmployeeIdList = selectedEmployeeIdList.filter { !dbEmployeeIdList.contains(it) }.toSet()

        // 添加角色员工
        if (addEmployeeIdList.isNotEmpty()) {
            sql.getAssociations(Employee::roles)
                .saveAll(addEmployeeIdList.map { employeeId ->
                    Tuple2(employeeId, roleId)
                })
        }
        return ResponseDTO.ok()
    }

    /**
     * 根据员工ID获取角色信息列表
     *
     * 此函数首先根据员工ID查询该员工已选择的角色ID，然后查询所有角色信息，
     * 并根据之前获取的角色ID列表设置每个角色是否被选择
     *
     * @param employeeId 员工ID，用于查询该员工已选择的角色
     * @return 返回一个RoleSelectedVO对象列表，每个对象包含角色信息和该角色是否被选择的状态
     */
    fun getRoleInfoListByEmployeeId(employeeId: String): List<RoleSelectedVO> {
        // 查询员工已选择的角色id
        val roleIds = roleRepository.byEmployeeId(employeeId) {
            select(table.roleId)
        }
        // 查询所有角色
        val vos = sql.createQuery(Role::class) {
            // RoleSelectedVO 来自 dto 文件定义
            select(table.fetch(RoleSelectedVO::class))
        }.execute()
        // 遍历所有角色，设置已选择的角色
        val newVos = vos.map {
            it.copy(
                // 这里有没有方案规避 copy??
                selected = roleIds.contains(it.roleId)
            )
        }
        return newVos
    }

    fun getRoleIdList(employeeId: String): List<RoleVO> {
        return roleRepository.byEmployeeId(employeeId) {
            select(table.fetch(RoleVO::class))
        }
    }

}