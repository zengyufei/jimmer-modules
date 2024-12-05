package com.zyf.repository

import com.zyf.employee.employeeId
import com.zyf.system.Role
import com.zyf.system.employees
import org.babyfish.jimmer.spring.repo.support.AbstractKotlinRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.query.KConfigurableRootQuery
import org.babyfish.jimmer.sql.kt.ast.query.KMutableRootQuery
import org.springframework.stereotype.Repository

@Repository
class RoleRepository(
    sql: KSqlClient
) : AbstractKotlinRepository<Role, String>(sql) {


    fun <R> byEmployeeId(
        inputEmployeeId: String?,
        block: KMutableRootQuery<Role>.() -> KConfigurableRootQuery<Role, R> = { select(table) as KConfigurableRootQuery<Role, R> }
    ): List<R> {
        val createQuery = sql.createQuery(Role::class) {
            inputEmployeeId?.let {
                where += table.employees {
                    employeeId eq it
                }
            }
            block(this)
        }
        return createQuery.execute()
    }

}
