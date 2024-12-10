package com.zyf.repository.support;

import com.zyf.repository.BaseRepository
import com.zyf.support.SerialNumberRecord
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository

/**
 * serial_number记录表(SerialNumberRecord)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-09 20:36:32
 */
 @Repository
class SerialNumberRecordRepository(
    sql: KSqlClient
) : BaseRepository<SerialNumberRecord, String>(sql) {


}

