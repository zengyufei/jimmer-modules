package com.zyf.repository.support;

import com.zyf.repository.BaseRepository
import com.zyf.support.SmartJob
import com.zyf.support.SmartJobLog
import com.zyf.support.copy
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * 定时任务配置 @listen(SmartJob)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-12 21:34:58
 */
 @Repository
class SmartJobRepository(
    sql: KSqlClient
) : BaseRepository<SmartJob, String>(sql) {


    /**
     * 保存执行记录
     *
     * @param logEntity
     * @param jobEntity
     */
    @Transactional(rollbackFor = [Throwable::class])
    fun saveLog(logEntity: SmartJobLog, jobEntity: SmartJob):String {
        val result = sql.insert(logEntity)

        sql.update(jobEntity.copy {
            lastExecuteLogId = result.modifiedEntity.logId
        })

        return  result.modifiedEntity.logId
    }
}

