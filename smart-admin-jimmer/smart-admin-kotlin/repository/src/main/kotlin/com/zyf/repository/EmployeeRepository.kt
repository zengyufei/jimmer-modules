package com.zyf.repository

import com.zyf.employee.*
import com.zyf.system.Role
import com.zyf.system.employees
import com.zyf.system.roleId
import org.babyfish.jimmer.spring.repo.support.AbstractKotlinRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.`eq?`
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import org.babyfish.jimmer.sql.kt.ast.query.KConfigurableRootQuery
import org.babyfish.jimmer.sql.kt.ast.query.KMutableRootQuery
import org.babyfish.jimmer.sql.kt.exists
import org.springframework.stereotype.Repository

@Repository
class EmployeeRepository(
    sql: KSqlClient
) : AbstractKotlinRepository<Employee, String>(sql) {

    fun existsId(employeeId: String): Boolean {
        return sql.exists(Employee::class) {
            where(table.employeeId eq employeeId)
        }
    }

    fun existsPhone(phone: String): Boolean {
        return sql.exists(Employee::class) {
            where(table.phone eq phone)
        }
    }

    /**
     * 根据登录名获取员工
     */
    fun byLoginName(loginName: String): Employee? {
        return sql.createQuery(Employee::class) {
            where(table.loginName eq loginName)
            select(table)
        }.fetchOneOrNull()
    }

    fun byPhone(phone: String): Employee? {
        return sql.createQuery(Employee::class) {
            where(table.phone eq phone)
            select(table)
        }.fetchOneOrNull()
    }


    fun byId(employeeId: String): Employee? {
        return sql.findById(Employee::class, employeeId)
    }


    fun <R> byRoleId(
        inputRoleId: String?,
        block: KMutableRootQuery<Employee>.() -> KConfigurableRootQuery<Employee, R> = { select(table) as KConfigurableRootQuery<Employee, R> }
    ): List<R> {
        val createQuery = sql.createQuery(Employee::class) {
            inputRoleId?.let {
                where += table.roles {
                    roleId eq it
                }
            }
            block(this)
        }
        return createQuery.execute()
    }

    fun <R> byRoleIds(
        inputRoleIds: List<String>,
        block: KMutableRootQuery<Employee>.() -> KConfigurableRootQuery<Employee, R> = { select(table) as KConfigurableRootQuery<Employee, R> }
    ): List<R> {
        val createQuery = sql.createQuery(Employee::class) {
            inputRoleIds.takeIf { it.isNotEmpty() }?.let {
                where += table.roles {
                    roleId valueIn it
                }
            }
            block(this)
        }
        return createQuery.execute()
    }
}
