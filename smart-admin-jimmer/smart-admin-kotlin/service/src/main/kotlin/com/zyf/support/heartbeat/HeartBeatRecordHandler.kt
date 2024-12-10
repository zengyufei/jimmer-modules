package com.zyf.support.heartbeat

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.utils.SmartBeanUtil
import com.zyf.support.heartbeat.core.IHeartBeatRecordHandler
import com.zyf.support.*
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Service

/**
 * 心跳记录
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-01-09 20:57:24
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Slf4j
@Service
class HeartBeatRecordHandler(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) : IHeartBeatRecordHandler {
    /**
     * 心跳日志处理方法
     * @param heartBeatRecord
     */
    override fun handler(heartBeatRecord: HeartBeatRecord) {
        val heartBeatRecordOld =  sql.createQuery(HeartBeatRecord::class) {
            where(table.projectPath eq heartBeatRecord.projectPath)
            where(table.serverIp eq heartBeatRecord.serverIp)
            where(table.processNo eq heartBeatRecord.processNo)
            select(table)
        }.fetchOneOrNull()
        heartBeatRecordOld?.let {
            sql.createUpdate(HeartBeatRecord::class) {
                set(table.heartBeatTime, heartBeatRecord.heartBeatTime)
                where(table.heartBeatRecordId eq heartBeatRecord.heartBeatRecordId)
            }.execute()
        }?: run {
            sql.insert(heartBeatRecord)
        }
    }
}
