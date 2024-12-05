package com.zyf.common.jimmer
import com.zyf.common.domain.PageResult
import org.babyfish.jimmer.sql.ast.impl.query.PageSource
import org.babyfish.jimmer.sql.ast.query.PageFactory
class CommonPageFactory<E> private constructor() : PageFactory<E, PageResult<E>> {
    override fun create(entities: List<E>, totalCount: Long, source: PageSource): PageResult<E> {
        val pageResult = PageResult<E>()
        pageResult.list = entities
        pageResult.pageNum = source.pageIndex.toLong() + 1L
        pageResult.pageSize = source.pageSize.toLong()
        pageResult.total = totalCount
        pageResult.pages = totalCount / source.pageSize + (if (totalCount % source.pageSize == 0L) 0 else 1)
        pageResult.emptyFlag = entities.isEmpty()
        return pageResult
    }
    companion object {
        private val INSTANCE: CommonPageFactory<*> = CommonPageFactory<Any>()

        fun <E> getInstance(): CommonPageFactory<E> {
            return INSTANCE as CommonPageFactory<E>
        }
    }
}