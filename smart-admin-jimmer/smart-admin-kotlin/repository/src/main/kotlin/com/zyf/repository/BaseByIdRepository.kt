package com.zyf.repository

import org.babyfish.jimmer.View
import kotlin.reflect.KClass


interface BaseByIdRepository<T : Any, E : Any> {

    fun byId(id: E): T?

}
