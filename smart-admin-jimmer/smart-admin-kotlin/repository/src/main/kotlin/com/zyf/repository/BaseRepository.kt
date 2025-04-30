package com.zyf.repository

import com.zyf.employee.Employee
import com.zyf.employee.loginName
import com.zyf.helpDoc.HelpDoc
import com.zyf.helpDoc.by
import io.lettuce.core.XReadArgs.Builder.block
import org.babyfish.jimmer.View
import org.babyfish.jimmer.spring.repo.support.AbstractKotlinRepository
import org.babyfish.jimmer.sql.fetcher.FetcherParser.FetcherContext
import org.babyfish.jimmer.sql.fetcher.impl.FetcherFactory
import org.babyfish.jimmer.sql.fetcher.impl.FetcherUtil
import org.babyfish.jimmer.sql.kt.ast.expression.KNonNullExpression
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.babyfish.jimmer.sql.kt.ast.query.KMutableRootQuery
import org.babyfish.jimmer.sql.kt.exists
import org.babyfish.jimmer.sql.kt.fetcher.FetcherCreator
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import java.io.Serializable
import kotlin.reflect.KClass


abstract class BaseRepository<T : Any, E : Any>(sql: org.babyfish.jimmer.sql.kt.KSqlClient) :
    AbstractKotlinRepository<T, E>(sql),
    BaseListRepository<T>,
    BaseByRepository<T>,
    BaseByIdRepository<T, E>,
    BaseByExistsRepository<T, E> {

    override fun notExistsId(id: E): Boolean {
        return sql.exists(entityType) {
            where(table.get<E>(immutableType.idProp) ne id)
        }
    }

    override fun notExistsBy(block: KMutableRootQuery<T>.() -> Unit): Boolean {
        return sql.exists(entityType) {
            block(this)
        }
    }

    override fun existsId(id: E): Boolean {
        return sql.exists(entityType) {
            where(table.get<E>(immutableType.idProp) eq id)
        }
    }

    override fun existsBy(block: KMutableRootQuery<T>.() -> Unit): Boolean {
        return sql.exists(entityType) {
            block(this)
        }
    }

    override fun by(block: KMutableRootQuery<T>.() -> Unit): T? {
        return sql.createQuery(entityType) {
            block(this)
            select(table)
        }.fetchOneOrNull()
    }

    override fun byId(id: E): T? {
        return sql.findById(entityType, id)
    }


    override fun listAll(block: KMutableRootQuery<T>.() -> Unit): List<T> {
        val createQuery = sql.createQuery(entityType) {
            block(this)
            select(table)
        }
        return createQuery.execute()
    }

    override fun <R : View<T>> listAll(clazz: KClass<R>, block: KMutableRootQuery<T>.() -> Unit): List<R> {
        val createQuery = sql.createQuery(entityType) {
            block(this)
            select(table.fetch(clazz))
        }
        return createQuery.execute()
    }

}
