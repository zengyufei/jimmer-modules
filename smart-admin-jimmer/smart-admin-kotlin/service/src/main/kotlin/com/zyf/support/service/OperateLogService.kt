package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.loginLog.OperateLog
import com.zyf.loginLog.createTime
import com.zyf.service.dto.OperateLogQueryForm
import com.zyf.service.dto.OperateLogVO
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.springframework.stereotype.Service

/**
 * 操作日志
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2021-12-08 20:48:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Service
class OperateLogService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) {

    /**
     * @author 罗伊
     * @description 分页查询
     */
    fun queryByPage(
        pageBean: PageBean,
        queryForm: OperateLogQueryForm
    ): ResponseDTO<PageResult<OperateLogVO>> {
        val pageResult = sql.createQuery(OperateLog::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(OperateLogVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 查询详情
     * @param operateLogId
     * @return
     */
    fun detail(operateLogId: String): ResponseDTO<OperateLogVO?> {
        val operateLog = sql.findById(OperateLogVO::class, operateLogId) ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)
        return ResponseDTO.ok(operateLog)
    }
}
