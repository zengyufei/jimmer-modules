package com.zyf.helpDoc.repository;

import com.zyf.helpDoc.HelpDocRelation;
import com.zyf.repository.BaseRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository

/**
 * 帮助文档-关联表(HelpDocRelation)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-14 16:48:23
 */
 @Repository
class HelpDocRelationRepository(
    sql: KSqlClient
) : BaseRepository<HelpDocRelation, String>(sql) {


}

