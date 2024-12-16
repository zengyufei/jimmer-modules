package com.zyf.helpDoc.repository;

import com.zyf.helpDoc.HelpDoc;
import com.zyf.repository.BaseRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository

/**
 * 帮助文档(HelpDoc)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-14 16:48:23
 */
 @Repository
class HelpDocRepository(
    sql: KSqlClient
) : BaseRepository<HelpDoc, String>(sql) {


}

