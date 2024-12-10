package com.zyf.repository.support;

import com.zyf.repository.BaseRepository
import com.zyf.support.ReloadItem
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository

/**
 * reload项目(ReloadItem)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-10 11:38:17
 */
 @Repository
class ReloadItemRepository(
    sql: KSqlClient
) : BaseRepository<ReloadItem, String>(sql) {


}

