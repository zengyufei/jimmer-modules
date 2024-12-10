package com.zyf.support.service;

import com.zyf.loginLog.LoginFail;
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.loginLog.updateTime
import com.zyf.oa.*
import com.zyf.service.dto.*
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 登录失败次数记录表(LoginFail)表服务实现类
 *
 * @author makejava
 * @since 2024-12-09 16:44:36
 */
@Slf4j
@Service("loginFailService")
class LoginFailService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper
) {


    /**
     * 分页查询登录失败次数记录表数据
     *
     * @param pageBean 分页信息，包括当前页码和每页大小
     * @param queryForm 查询条件，用于筛选登录失败次数记录表数据
     * @return 包含分页结果的响应对象，其中包含登录失败次数记录表数据列表和分页信息
     */
    fun queryByPage(pageBean: PageBean, queryForm: LoginFailQueryForm): ResponseDTO<PageResult<LoginFailVO>> {
        val pageResult = sql.createQuery(LoginFail::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.updateTime.desc())

            where(queryForm)
            select(table.fetch(LoginFailVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 删除指定ID的登录失败次数记录表
     *
     * @param loginFailId 登录失败次数记录表唯一标识符
     * @return 响应对象，包含删除结果的提示信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun batchDelete(loginFailIds: List<String>): ResponseDTO<String?> {
        sql.deleteByIds(LoginFail::class, loginFailIds)
        return ResponseDTO.ok()
    }

}
