package com.zyf.repository

import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.kt.ast.query.KMutableRootQuery
import kotlin.reflect.KClass


interface BaseListRepository<T : Any> {

    fun listAll(
        block: KMutableRootQuery<T>.() -> Unit = {
            select(table)
        }
    ): List<T>


    fun <R : View<T>> listAll(
        clazz: KClass<R>,
        block: KMutableRootQuery<T>.() -> Unit = {
            select(table.fetch(clazz))
        }
    ): List<R>

}
