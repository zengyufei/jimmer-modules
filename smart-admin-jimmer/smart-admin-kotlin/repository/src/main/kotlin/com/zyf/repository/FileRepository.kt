package com.zyf.repository

import com.zyf.support.FileInfo
import com.zyf.support.fileKey
import org.babyfish.jimmer.spring.repo.support.AbstractKotlinRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import org.babyfish.jimmer.sql.kt.ast.expression.`valueIn?`
import org.babyfish.jimmer.sql.kt.ast.query.KConfigurableRootQuery
import org.babyfish.jimmer.sql.kt.ast.query.KMutableRootQuery
import org.springframework.stereotype.Repository

@Repository
class FileRepository(
    sql: KSqlClient
) : AbstractKotlinRepository<FileInfo, String>(sql) {


    fun <R> byFileKey(
        inputFileKey: String?,
        block: KMutableRootQuery<FileInfo>.() -> KConfigurableRootQuery<FileInfo, R> = { select(table) as KConfigurableRootQuery<FileInfo, R> }
    ): R? {
        val createQuery = sql.createQuery(FileInfo::class) {
            where(table.fileKey eq inputFileKey)
            block(this)
        }
        return createQuery.fetchOneOrNull()
    }


    fun <R> byFileKeys(
        inputFileKeys: List<String?>,
        block: KMutableRootQuery<FileInfo>.() -> KConfigurableRootQuery<FileInfo, R> = { select(table) as KConfigurableRootQuery<FileInfo, R> }
    ): List<R> {
        val createQuery = sql.createQuery(FileInfo::class) {
            inputFileKeys
                .takeIf { it.isNotEmpty() }
                ?.filterNotNull()
                ?.let {
                    where(table.fileKey `valueIn?` it)
                }
            block(this)
        }
        return createQuery.execute()
    }

}
