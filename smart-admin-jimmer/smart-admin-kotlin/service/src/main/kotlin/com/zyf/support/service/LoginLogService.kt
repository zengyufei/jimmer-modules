package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.LoginLogResultEnum
import com.zyf.common.enums.UserTypeEnum
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.loginLog.*
import com.zyf.service.dto.LoginLogQueryForm
import com.zyf.service.dto.LoginLogVO
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Service

/**
 * 登录日志
 */
@Slf4j
@Service
class LoginLogService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) {

    /**
     * 分页查询
     */
    fun queryByPage(
        pageBean: PageBean,
        queryForm: LoginLogQueryForm
    ): ResponseDTO<PageResult<LoginLogVO>> {
        val pageResult = sql.createQuery(LoginLog::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(LoginLogVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 添加
     */
    fun log(loginLog: LoginLog) {
        try {
            sql.insert(loginLog)
        } catch (e: Throwable) {
            log.error(e.message, e)
        }
    }

    /**
     * 查询上一个登录记录
     */
    fun queryLastByUserId(
        userId: String,
        userTypeEnum: UserTypeEnum,
        loginLogResultEnum: LoginLogResultEnum
    ): LoginLogVO {
        return sql.findOne(LoginLogVO::class) {
            orderBy(table.loginLogId.desc())
            where(
                table.userId eq userId,
                table.userType eq userTypeEnum.value,
                table.loginResult eq loginLogResultEnum.value
            )
        }
    }
}