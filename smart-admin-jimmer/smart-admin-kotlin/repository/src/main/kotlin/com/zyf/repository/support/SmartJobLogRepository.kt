package com.zyf.repository.support;

import com.zyf.support.SmartJobLog;
import com.zyf.repository.BaseRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository

/**
 * 定时任务-执行记录 @listen(SmartJobLog)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-12 21:34:58
 */
@Repository
class SmartJobLogRepository(
    sql: KSqlClient
) : BaseRepository<SmartJobLog, String>(sql) {


}

