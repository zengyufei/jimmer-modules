package com.zyf.repository

import org.babyfish.jimmer.View
import kotlin.reflect.KClass


interface BaseByIdRepository<T : Any, E : Any> {

    fun byId(id: E): T?

    fun <V : View<T>> byId(
        viewType: KClass<V>,
        id: E,
        block: (() -> org.babyfish.jimmer.sql.fetcher.Fetcher<V>)? = null
    ): V?

}
