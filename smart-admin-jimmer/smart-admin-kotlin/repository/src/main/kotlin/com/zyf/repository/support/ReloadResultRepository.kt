package com.zyf.repository.support;

import com.zyf.repository.BaseRepository
import com.zyf.support.ReloadResult
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository

/**
 * reload结果(ReloadResult)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-10 11:38:18
 */
 @Repository
class ReloadResultRepository(
    sql: KSqlClient
) : BaseRepository<ReloadResult, String>(sql) {


}

