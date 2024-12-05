package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.RequestUser
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.UserTypeEnum
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.common.utils.SmartBeanUtil
import com.zyf.oa.Enterprise
import com.zyf.oa.createTime
import com.zyf.service.dto.EnterpriseVO
import com.zyf.service.dto.FeedbackAddForm
import com.zyf.service.dto.FeedbackQueryForm
import com.zyf.service.dto.FeedbackVO
import com.zyf.support.Feedback
import com.zyf.support.createTime
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.springframework.stereotype.Service

/**
 * 意见反馈
 *
 * @Author 1024创新实验室: 开云
 * @Date 2022-08-11 20:48:09
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Service
class FeedbackService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) {

    /**
     * 分页查询
     */
    fun query(
        pageBean: PageBean,
        queryForm: FeedbackQueryForm
    ): ResponseDTO<PageResult<FeedbackVO>> {
        val pageResult = sql.createQuery(Feedback::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(FeedbackVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 新建
     */
    fun add(addForm: FeedbackAddForm, requestUser: RequestUser?): ResponseDTO<String?> {
        sql.insert(addForm.toEntity {
            userType = requestUser?.userType?.value ?: UserTypeEnum.ADMIN_EMPLOYEE.value
            userName = requestUser?.userName ?: "system"
        })
        return ResponseDTO.ok()
    }
} 