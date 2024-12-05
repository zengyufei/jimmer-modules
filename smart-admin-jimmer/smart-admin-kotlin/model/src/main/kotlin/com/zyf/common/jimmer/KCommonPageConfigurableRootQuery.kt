package com.zyf.common.jimmer

import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import org.babyfish.jimmer.sql.ast.query.Order
import org.babyfish.jimmer.sql.ast.tuple.Tuple2
import org.babyfish.jimmer.sql.kt.ast.query.KConfigurableRootQuery
import org.babyfish.jimmer.sql.kt.ast.query.KMutableQuery
import org.babyfish.jimmer.sql.kt.ast.table.impl.KTableImplementor
import java.sql.Connection

fun <E> KConfigurableRootQuery<*, E>.page(
    pageNum: Int,
    pageSize: Int,
    con: Connection? = null
): PageResult<E> =
    fetchPage(
        pageNum - 1,
        pageSize,
        con,
        CommonPageFactory.getInstance()
    )

fun <E> KConfigurableRootQuery<*, E>.page(
    pageBean: PageBean?,
    con: Connection? = null
): PageResult<E> =
    if (pageBean === null || !pageBean.searchCount) {
        page(0, Int.MAX_VALUE, con)
    } else {
        page(pageBean.pageNum, pageBean.pageSize, con)
    }


fun <T1, T2> KConfigurableRootQuery<*, Tuple2<T1, T2>>.page(
    pageBean: PageBean?,
    con: Connection? = null,
    block: (Tuple2<T1, T2>) -> T1
): PageResult<T1> {
    val pageResult = if (pageBean === null || !pageBean.searchCount) {
        page(0, Int.MAX_VALUE, con)
    } else {
        page(pageBean.pageNum, pageBean.pageSize, con)
    }
    val newPageResult = PageResult<T1>()
    newPageResult.pageNum = pageResult.pageNum
    newPageResult.pageSize = pageResult.pageSize
    newPageResult.pages = pageResult.pages
    newPageResult.total = pageResult.total
    newPageResult.emptyFlag = pageResult.emptyFlag
    newPageResult.list = pageResult.list?.map { block(it) }
    return newPageResult
}

fun KMutableQuery<*>.orderBy(pageBean: PageBean) {
    pageBean.sortCode?.let {
        orderBy(Order.makeOrders((table as KTableImplementor<*>).javaTable, pageBean.sortCode))
    }
}


fun KMutableQuery<*>.orderByIf(condition: Boolean, pageBean: PageBean) {
    if (condition) {
        orderBy(pageBean)
    }
}

