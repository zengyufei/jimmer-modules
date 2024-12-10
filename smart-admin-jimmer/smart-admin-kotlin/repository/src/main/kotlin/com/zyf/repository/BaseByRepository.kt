package com.zyf.repository

import org.babyfish.jimmer.sql.kt.ast.query.KMutableRootQuery


interface BaseByRepository<T : Any> {

    fun by(block: KMutableRootQuery<T>.() -> Unit): T?
}
