package com.zyf.repository.support;

import com.zyf.support.MailTemplate;
import com.zyf.repository.BaseRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository

/**
 * (MailTemplate)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-21 16:56:20
 */
 @Repository
class MailTemplateRepository(
    sql: KSqlClient
) : BaseRepository<MailTemplate, String>(sql) {


}

