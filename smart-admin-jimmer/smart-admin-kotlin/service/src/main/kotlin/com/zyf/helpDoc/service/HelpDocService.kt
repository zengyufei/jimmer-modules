package com.zyf.helpDoc.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.helpDoc.*
import com.zyf.helpDoc.domain.HelpDocRelationVO
import com.zyf.repository.helpDoc.HelpDocCatalogRepository
import com.zyf.repository.helpDoc.HelpDocRelationRepository
import com.zyf.repository.helpDoc.HelpDocRepository
import com.zyf.repository.helpDoc.HelpDocViewRecordRepository
import com.zyf.service.dto.*
import org.babyfish.jimmer.sql.DissociateAction
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
class HelpDocService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val helpDocCatalogRepository: HelpDocCatalogRepository,
    val helpDocRepository: HelpDocRepository,
    val helpDocRelationRepository: HelpDocRelationRepository,
    val helpDocViewRecordRepository: HelpDocViewRecordRepository,
) {


    /**
     * 查询 帮助文档
     *
     * @param queryForm
     * @return
     */
    fun query(
        pageBean: PageBean,
        queryForm: HelpDocQueryForm?): PageResult<HelpDocVO> {
        val pageResult = sql.createQuery(HelpDoc::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.sort.asc(), table.createTime.desc())

            where(queryForm)
            select(table.fetch(HelpDocVO::class))
        }.page(pageBean)
        return pageResult
    }

    /**
     * 添加
     *
     * @param addForm
     * @return
     */
    @Transactional(rollbackFor = [Exception::class])
    fun add(addForm: HelpDocAddForm): ResponseDTO<String?> {
        // 更新
        val result = sql.insert(addForm)
        val inputHelpDocId = result.modifiedEntity.helpDocId

        if (addForm.relationList.isNotEmpty()) {
            val newList = addForm.relationList.map {
                it.toEntity {
                    helpDocId = inputHelpDocId
                }
            }.toList()
            sql.saveEntities(newList)
        }

        return ResponseDTO.ok()
    }


    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    @Transactional(rollbackFor = [Exception::class])
    fun update(updateForm: HelpDocUpdateForm): ResponseDTO<String?> {
        // 更新
        sql.update(updateForm)

        if (updateForm.relationList.isNotEmpty()) {
            sql.createDelete(HelpDocRelation::class) {
                where(table.helpDocId eq updateForm.helpDocId)
            }.execute()
            val newList = updateForm.relationList.map {
                it.toEntity {
                    helpDocId = updateForm.helpDocId
                }
            }.toList()
            sql.saveEntities(newList)
        }

        return ResponseDTO.ok()
    }


    /**
     * 删除
     *
     * @param helpDocId
     * @return
     */
    @Transactional(rollbackFor = [Exception::class])
    fun delete(helpDocId: String?): ResponseDTO<String?> {
        if (helpDocId == null) {
            return ResponseDTO.ok()
        }
        sql.entities.delete(HelpDoc::class, helpDocId) {
            setDissociateAction(HelpDocRelation::helpDoc, DissociateAction.DELETE)
        }
//        helpDocRepository.deleteById(helpDocId)
//        sql.createDelete(HelpDocRelation::class) {
//            where(table.helpDocId eq helpDocId)
//        }.execute()
        return ResponseDTO.ok()
    }

    /**
     * 获取详情
     *
     * @param helpDocId
     * @return
     */
    fun getDetail(helpDocId: String?): HelpDocDetailVO? {
        if (helpDocId == null) {
            return null
        }
        val detail: HelpDocDetailVO? = helpDocRepository.byId(HelpDocDetailVO::class, helpDocId)
        if (detail != null) {
            val listAll = helpDocRelationRepository.listAll(HelpDocRelationVO::class) {
                where(table.helpDocId eq helpDocId)
            }
            val copy = detail.copy(
                relationList = listAll
            )
            return copy
        }
        return null
    }


    /**
     * 获取详情
     *
     * @param inputRelationId
     * @return
     */
    fun queryHelpDocByRelationId(inputRelationId: String): List<HelpDocVO> {
        return sql.createQuery(HelpDoc::class) {
            where += table.helpDocRelations {
                relationId eq inputRelationId
            }
            select(table.fetch(HelpDocVO::class))
        }.execute()
    }


}