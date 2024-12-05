package com.zyf.repository

import com.zyf.employee.*
import org.babyfish.jimmer.View
import org.babyfish.jimmer.spring.repo.support.AbstractKotlinRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.exists
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.stereotype.Repository
import kotlin.reflect.KClass

@Repository
class DepartmentRepository(
    sql: KSqlClient
) : AbstractKotlinRepository<Department, String>(sql) {

    fun existsId(departmentId: String): Boolean {
        return sql.exists(Department::class) {
            where(table.departmentId eq departmentId)
        }
    }


    fun byId(departmentId: String): Department? {
        return sql.findById(Department::class, departmentId)
    }
    fun <V : View<Department>> byId(vo: KClass<V>, departmentId: String): V? {
        return sql.findById(vo, departmentId)
    }

    fun <V : View<Department>> listAll(vo: KClass<V>): List<V> {
        return sql.findAll(vo)
    }

    fun listAll(): List<Department> {
       return  sql.entities.findAll(Department::class)
    }
}
