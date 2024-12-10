package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.repository.support.SerialNumberRecordRepository
import com.zyf.service.dto.SerialNumberRecordQueryForm
import com.zyf.service.dto.SerialNumberRecordVO
import com.zyf.support.SerialNumberRecord
import com.zyf.support.lastTime
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.springframework.stereotype.Service

/**
 * serial_number记录表(SerialNumberRecord)表服务实现类
 *
 * @author makejava
 * @since 2024-12-09 20:36:32
 */
@Slf4j
@Service("serialNumberRecordService")
class SerialNumberRecordService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val serialNumberRecordRepository: SerialNumberRecordRepository,
) {


    /**
     * 分页查询serial_number记录表数据
     *
     * @param pageBean 分页信息，包括当前页码和每页大小
     * @param queryForm 查询条件，用于筛选serial_number记录表数据
     * @return 包含分页结果的响应对象，其中包含serial_number记录表数据列表和分页信息
     */
    fun queryByPage(pageBean: PageBean, queryForm: SerialNumberRecordQueryForm): ResponseDTO<PageResult<SerialNumberRecordVO>> {
        val pageResult = sql.createQuery(SerialNumberRecord::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.lastTime.desc())

            where(queryForm)
            select(table.fetch(SerialNumberRecordVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

}
