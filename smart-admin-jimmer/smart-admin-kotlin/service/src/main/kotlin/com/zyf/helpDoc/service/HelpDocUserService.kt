package com.zyf.helpDoc.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.RequestUser
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.helpDoc.*
import com.zyf.helpDoc.service.dto.HelpDocViewRecordQueryForm
import com.zyf.helpDoc.service.dto.HelpDocViewRecordVO
import com.zyf.repository.helpDoc.HelpDocCatalogRepository
import com.zyf.repository.helpDoc.HelpDocRelationRepository
import com.zyf.repository.helpDoc.HelpDocRepository
import com.zyf.repository.helpDoc.HelpDocViewRecordRepository
import com.zyf.service.dto.ChangeLogVO
import com.zyf.service.dto.HelpDocDetailVO
import com.zyf.service.dto.HelpDocVO
import com.zyf.support.ChangeLog
import com.zyf.support.createTime
import jakarta.annotation.Resource
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.count
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.plus
import org.springframework.stereotype.Service

/**
 * 用户查看  帮助文档
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-08-20 23:11:42
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Service
class HelpDocUserService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val helpDocCatalogRepository: HelpDocCatalogRepository,
    val helpDocRepository: HelpDocRepository,
    val helpDocRelationRepository: HelpDocRelationRepository,
    val helpDocViewRecordRepository: HelpDocViewRecordRepository,
) {


    /**
     * 查询全部 帮助文档
     *
     * @return
     */
    fun queryAllHelpDocList(): ResponseDTO<List<HelpDocVO>> {
        return ResponseDTO.ok(helpDocRepository.listAll(HelpDocVO::class))
    }


    /**
     * 查询我的 待查看的 帮助文档清单
     *
     * @return
     */
    fun view(requestUser: RequestUser?, inputHelpDocId: String?): ResponseDTO<HelpDocDetailVO?> {
        if (inputHelpDocId == null) {
            return ResponseDTO.userErrorParam("帮助文档id不能为空")
        }
        val helpDocDetailVO = helpDocRepository.byId(HelpDocDetailVO::class, inputHelpDocId) ?: return ResponseDTO.userErrorParam("帮助文档不存在")

        val viewCount = sql.createQuery(HelpDocViewRecord::class) {
            where(table.helpDocId eq inputHelpDocId)
            where(table.userId eq requestUser!!.userId)
            select(count(table))
        }.fetchUnlimitedCount()
        if (viewCount == 0L) {
            sql.insert(HelpDocViewRecord {
                helpDocId = inputHelpDocId
                userId = requestUser!!.userId
                userName = requestUser.userName
                firstIp = requestUser.ip
                firstUserAgent = requestUser.userAgent
                pageViewCount = 1
            })
            sql.createUpdate(HelpDoc::class) {
                set(table.pageViewCount, table.pageViewCount+1)
                set(table.userViewCount, table.userViewCount+1)
                where(table.helpDocId eq inputHelpDocId)
            }.execute()
            return ResponseDTO.ok(helpDocDetailVO.copy(
                pageViewCount = helpDocDetailVO.pageViewCount+1,
                userViewCount = helpDocDetailVO.userViewCount+1
            ))
        } else {
            sql.createUpdate(HelpDocViewRecord::class) {
                set(table.pageViewCount, table.pageViewCount+1)
                set(table.lastIp, requestUser!!.ip)
                set(table.lastUserAgent, requestUser.userAgent)
                where(table.helpDocId eq inputHelpDocId)
                where(table.userId eq requestUser.userId)
            }.execute()
            sql.createUpdate(HelpDoc::class) {
                set(table.userViewCount, table.userViewCount+1)
                where(table.helpDocId eq inputHelpDocId)
            }.execute()
            return ResponseDTO.ok(helpDocDetailVO.copy(
                pageViewCount = helpDocDetailVO.pageViewCount+1,
            ))
        }
    }


    /**
     * 分页查询  查看记录
     *
     * @param helpDocViewRecordQueryForm
     * @return
     */
    fun queryViewRecord(
        pageBean: PageBean,
        helpDocViewRecordQueryForm: HelpDocViewRecordQueryForm
    ): PageResult<HelpDocViewRecordVO> {
        val pageResult = sql.createQuery(HelpDocViewRecord::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.updateTime.desc(), table.createTime.desc())

            where(helpDocViewRecordQueryForm)
            select(table.fetch(HelpDocViewRecordVO::class))
        }.page(pageBean)
        return pageResult
    }
}
