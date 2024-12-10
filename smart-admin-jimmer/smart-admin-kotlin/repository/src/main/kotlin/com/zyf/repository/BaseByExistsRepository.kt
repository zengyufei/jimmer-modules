package com.zyf.repository

import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.kt.ast.query.KMutableRootQuery
import kotlin.reflect.KClass


interface BaseByExistsRepository<T : Any, E : Any> {

    fun notExistsId(id: E): Boolean

    fun notExistsBy(block: KMutableRootQuery<T>.() -> Unit): Boolean

    fun existsId(id: E): Boolean

    fun existsBy(block: KMutableRootQuery<T>.() -> Unit): Boolean

}
