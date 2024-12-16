package com.zyf.helpDoc.repository;

import com.zyf.helpDoc.HelpDocViewRecord;
import com.zyf.repository.BaseRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository

/**
 * 帮助文档-查看记录(HelpDocViewRecord)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-14 16:24:54
 */
 @Repository
class HelpDocViewRecordRepository(
    sql: KSqlClient
) : BaseRepository<HelpDocViewRecord, String>(sql) {


}

