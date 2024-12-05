package com.zyf.system.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.ResponseDTO
import com.zyf.employee.Employee
import com.zyf.employee.roles
import com.zyf.service.dto.RoleAddForm
import com.zyf.service.dto.RoleUpdateForm
import com.zyf.service.dto.RoleVO
import com.zyf.system.Role
import com.zyf.system.roleId
import com.zyf.system.roleName
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.count
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.babyfish.jimmer.sql.kt.exists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
class RoleService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) {

    /**
     * 添加角色功能的实现
     * 此函数首先检查要添加的角色名称是否已存在于数据库中，以避免重复的角色名称
     * 然后检查角色编码是否重复，以确保每个角色的唯一性
     * 如果上述检查通过，则将新角色插入到数据库中
     *
     * @param roleAddForm 包含要添加角色的信息，包括角色名称等
     * @return 返回一个ResponseDTO对象，包含操作结果的字符串信息
     */
    fun addRole(roleAddForm: RoleAddForm): ResponseDTO<String?> {
        // 检查角色名称是否重复
        val fetchOneRoleName = sql.createQuery(Role::class) {
            where(table.roleName eq roleAddForm.roleName)
            select(count(table))
        }.fetchOne()
        if (fetchOneRoleName > 0) {
            return ResponseDTO.userErrorParam("角色名称重复")
        }

        // 检查角色编码是否重复，并获取重复的角色名称
        sql.createQuery(Role::class) {
            where(table.roleName eq roleAddForm.roleName)
            select(table.roleName)
        }.fetchOneOrNull()?.let {
            return ResponseDTO.userErrorParam("角色编码重复，重复的角色为：${it}")
        }

        // 插入新角色到数据库
        sql.insert(roleAddForm)
        // 返回成功响应
        return ResponseDTO.ok()
    }

    /**
     * 根据角色id 删除
     */
    @Transactional(rollbackFor = [Exception::class])
    fun deleteRole(inputRoleId: String): ResponseDTO<String?> {
        sql.findById(Role::class, inputRoleId) ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)

        // 当没有员工绑定这个角色时才可以以删除
        if (sql.exists(Employee::class) {
                where(table.roles { roleId eq inputRoleId })
            }) {
            return ResponseDTO.error(UserErrorCode.ALREADY_EXIST, "该角色下存在员工，无法删除")
        }

        sql.deleteById(Role::class, inputRoleId)
        return ResponseDTO.ok()
    }

    /**
     * 更新角色
     */
    @Transactional(rollbackFor = [Exception::class])
    fun updateRole(roleUpdateForm: RoleUpdateForm): ResponseDTO<String?> {
        sql.findById(Role::class, roleUpdateForm.roleId!!) ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)

        val fetchOneRoleName = sql.createQuery(Role::class) {
            where(table.roleName eq roleUpdateForm.roleName)
            where(table.roleId ne roleUpdateForm.roleId)
            select(count(table))
        }.fetchOne()
        if (fetchOneRoleName > 0) {
            return ResponseDTO.userErrorParam("角色名称重复")
        }

        sql.createQuery(Role::class) {
            where(table.roleName eq roleUpdateForm.roleName)
            where(table.roleId ne roleUpdateForm.roleId)
            select(table.roleName)
        }.fetchOneOrNull()?.let {
            return ResponseDTO.userErrorParam("角色编码重复，重复的角色为：${it}")
        }

        sql.update(roleUpdateForm)
        return ResponseDTO.ok()
    }

    /**
     * 根据id获取角色数据
     */
    fun getRoleById(roleId: Long): ResponseDTO<RoleVO?> {
        val roleVo = sql.findById(RoleVO::class, roleId) ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)
        return ResponseDTO.ok(roleVo)
    }

    /**
     * 获取所有角色列表
     */
    fun getAllRole(): ResponseDTO<List<RoleVO>> {
        val vos = sql.createQuery(Role::class) {
            select(table.fetch(RoleVO::class))
        }.execute()
        return ResponseDTO.ok(vos)
    }

}