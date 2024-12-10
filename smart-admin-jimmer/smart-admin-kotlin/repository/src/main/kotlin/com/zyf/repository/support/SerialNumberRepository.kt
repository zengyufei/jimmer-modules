package com.zyf.repository.support;

import com.zyf.repository.BaseRepository
import com.zyf.support.SerialNumber
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository

/**
 * 单号生成器定义表(SerialNumber)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-09 20:36:31
 */
 @Repository
class SerialNumberRepository(
    sql: KSqlClient
) : BaseRepository<SerialNumber, String>(sql) {


}

