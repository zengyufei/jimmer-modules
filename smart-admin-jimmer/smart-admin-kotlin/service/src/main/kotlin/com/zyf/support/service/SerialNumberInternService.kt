package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.Interner
import com.google.common.collect.Interners
import com.zyf.repository.support.SerialNumberRecordRepository
import com.zyf.repository.support.SerialNumberRepository
import com.zyf.support.SerialNumber
import com.zyf.support.domain.SerialNumberGenerateResultBO
import com.zyf.support.domain.SerialNumberInfoBO
import com.zyf.support.domain.SerialNumberLastGenerateBO
import com.zyf.support.lastNumber
import com.zyf.support.lastTime
import com.zyf.support.serialNumberId
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

/**
 * 单据序列号 基于内存锁实现
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-03-25 21:46:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Service
class SerialNumberInternService(
    override val sql: KSqlClient,
    override val objectMapper: ObjectMapper,
    override val serialNumberRepository: SerialNumberRepository,
    override val serialNumberRecordRepository: SerialNumberRecordRepository,
) : SerialNumberBaseService(sql, objectMapper, serialNumberRepository, serialNumberRecordRepository) {

    private val serialNumberLastGenerateMap = ConcurrentHashMap<String, SerialNumberLastGenerateBO>()

    override fun initLastGenerateData(serialNumberList: List<SerialNumber>) {

        for (serialNumber in serialNumberList) {
            val lastGenerateBO = SerialNumberLastGenerateBO()
            lastGenerateBO.serialNumberId = serialNumber.serialNumberId
            lastGenerateBO.lastNumber = serialNumber.lastNumber
            lastGenerateBO.lastTime = serialNumber.lastTime
            serialNumberLastGenerateMap[serialNumber.serialNumberId] = lastGenerateBO
        }
    }

    override fun generateSerialNumberList(serialNumber: SerialNumberInfoBO, count: Int): List<String> {
        var serialNumberGenerateResult: SerialNumberGenerateResultBO
        synchronized(POOL.intern(serialNumber.serialNumberId!!)) {
            // 获取上次的生成结果
            val lastGenerateBO = serialNumberLastGenerateMap.get(serialNumber.serialNumberId)

            // 生成
            serialNumberGenerateResult = super.loopNumberList(lastGenerateBO!!, serialNumber, count)

            // 将生成信息保存的内存和数据库
            lastGenerateBO.lastNumber = serialNumberGenerateResult.lastNumber
            lastGenerateBO.lastTime = serialNumberGenerateResult.lastTime
            sql.createUpdate(SerialNumber::class) {
                set(table.lastNumber, serialNumberGenerateResult.lastNumber)
                set(table.lastTime, serialNumberGenerateResult.lastTime!!)
                where(table.serialNumberId eq serialNumber.serialNumberId)
            }.execute()

            // 把生成过程保存到数据库里
            super.saveRecord(serialNumberGenerateResult)
        }

        return formatNumberList(serialNumberGenerateResult, serialNumber)
    }


    companion object {
        /**
         * 按照 serialNumberId 进行锁
         */
        private val POOL: Interner<String> = Interners.newStrongInterner()
    }
}
