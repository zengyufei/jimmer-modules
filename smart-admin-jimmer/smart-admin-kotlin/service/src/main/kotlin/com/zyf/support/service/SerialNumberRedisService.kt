package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.constant.RedisKeyConst
import com.zyf.common.exception.BusinessException
import com.zyf.repository.support.SerialNumberRecordRepository
import com.zyf.repository.support.SerialNumberRepository
import com.zyf.runtime.support.redis.RedisService
import com.zyf.support.SerialNumber
import com.zyf.support.domain.SerialNumberGenerateResultBO
import com.zyf.support.domain.SerialNumberInfoBO
import com.zyf.support.domain.SerialNumberLastGenerateBO
import com.zyf.support.lastNumber
import com.zyf.support.lastTime
import com.zyf.support.serialNumberId
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq

/**
 * 单据序列号 基于redis锁实现
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-03-25 21:46:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
@Slf4j
class SerialNumberRedisService(
    override val sql: KSqlClient,
    override val objectMapper: ObjectMapper,
    override val serialNumberRepository: SerialNumberRepository,
    override val serialNumberRecordRepository: SerialNumberRecordRepository,
    private val redisService: RedisService
) : SerialNumberBaseService(sql, objectMapper, serialNumberRepository, serialNumberRecordRepository) {

    override fun initLastGenerateData(serialNumberList: List<SerialNumber>) {

        // 删除之前的
        redisService.delete(RedisKeyConst.Support.SERIAL_NUMBER_LAST_INFO)

        for (serialNumber in serialNumberList) {
            val lastGenerateBO = SerialNumberLastGenerateBO()
            lastGenerateBO.serialNumberId = serialNumber.serialNumberId
            lastGenerateBO.lastNumber = serialNumber.lastNumber
            lastGenerateBO.lastTime = serialNumber.lastTime

            redisService.mset(
                RedisKeyConst.Support.SERIAL_NUMBER_LAST_INFO,
                java.lang.String.valueOf(serialNumber.serialNumberId),
                lastGenerateBO
            )
        }
    }

    override fun generateSerialNumberList(serialNumber: SerialNumberInfoBO, count: Int): List<String> {
        val serialNumberGenerateResult: SerialNumberGenerateResultBO?
        val lockKey: String = RedisKeyConst.Support.SERIAL_NUMBER + serialNumber.serialNumberId
        try {
            var lock = false
            for (i in 0 until MAX_GET_LOCK_COUNT) {
                try {
                    lock = redisService.getLock(lockKey, 60 * 1000L)
                    if (lock) {
                        break
                    }
                    Thread.sleep(SLEEP_MILLISECONDS)
                } catch (e: Throwable) {
                    log.error(e.message, e)
                }
            }
            if (!lock) {
                throw BusinessException("SerialNumber 尝试5次，未能生成单号")
            }
            // 获取上次的生成结果
            val lastGenerateBO: SerialNumberLastGenerateBO = redisService.mget(
                RedisKeyConst.Support.SERIAL_NUMBER_LAST_INFO,
                serialNumber.serialNumberId
            ) as SerialNumberLastGenerateBO

            // 生成
            serialNumberGenerateResult = super.loopNumberList(lastGenerateBO, serialNumber, count)

            // 将生成信息保存的内存和数据库
            lastGenerateBO.lastNumber = serialNumberGenerateResult.lastNumber
            lastGenerateBO.lastTime = serialNumberGenerateResult.lastTime
            sql.createUpdate(SerialNumber::class) {
                set(table.lastNumber, serialNumberGenerateResult.lastNumber!!)
                set(table.lastTime, serialNumberGenerateResult.lastTime!!)
                where(table.serialNumberId eq serialNumber.serialNumberId)
            }.execute()

            redisService.mset(
                RedisKeyConst.Support.SERIAL_NUMBER_LAST_INFO,
                java.lang.String.valueOf(serialNumber.serialNumberId), lastGenerateBO
            )

            // 把生成过程保存到数据库里
            super.saveRecord(serialNumberGenerateResult)
        } catch (e: Throwable) {
            log.error(e.message, e)
            throw e
        } finally {
            redisService.unLock(lockKey)
        }

        if (serialNumberGenerateResult == null) {
            return emptyList()
        }

        return formatNumberList(serialNumberGenerateResult, serialNumber)
    }

    companion object {
        private const val MAX_GET_LOCK_COUNT = 5

        private const val SLEEP_MILLISECONDS = 200L
    }
}
