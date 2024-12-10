package com.zyf.support.heartbeat

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.service.dto.HeartBeatRecordQueryForm
import com.zyf.service.dto.HeartBeatRecordVO
import com.zyf.support.heartBeatTime
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Service
import com.zyf.support.HeartBeatRecord
import org.babyfish.jimmer.sql.kt.ast.expression.*

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
class HeartBeatService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) {

    fun pageQuery(pageBean: PageBean, queryForm: HeartBeatRecordQueryForm): ResponseDTO<PageResult<HeartBeatRecordVO>> {
        val pageResult = sql.createQuery(HeartBeatRecord::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.heartBeatTime.desc())
            where(queryForm)
            select(table.fetch(HeartBeatRecordVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }
}
