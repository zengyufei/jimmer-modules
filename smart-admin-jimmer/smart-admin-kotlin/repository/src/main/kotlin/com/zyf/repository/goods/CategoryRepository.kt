package com.zyf.repository.goods

import com.zyf.goods.Category
import com.zyf.repository.BaseRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository

@Repository
class CategoryRepository(
    sql: KSqlClient
) : BaseRepository<Category, String>(sql) {


}
