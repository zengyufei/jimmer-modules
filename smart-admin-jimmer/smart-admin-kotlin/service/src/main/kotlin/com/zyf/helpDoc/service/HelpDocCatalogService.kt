package com.zyf.helpDoc.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.domain.ResponseDTO
import com.zyf.helpDoc.HelpDoc
import com.zyf.helpDoc.HelpDocCatalog
import com.zyf.helpDoc.helpDocCatalogId
import com.zyf.repository.helpDoc.HelpDocCatalogRepository
import com.zyf.repository.helpDoc.HelpDocRelationRepository
import com.zyf.repository.helpDoc.HelpDocRepository
import com.zyf.repository.helpDoc.HelpDocViewRecordRepository
import com.zyf.service.dto.HelpDocCatalogAddForm
import com.zyf.service.dto.HelpDocCatalogUpdateForm
import com.zyf.service.dto.HelpDocCatalogVO
import com.zyf.service.dto.HelpDocVO
import org.apache.commons.collections4.CollectionUtils
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Service

/**
 * 帮助文档 目录
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-08-20 23:11:42
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Service
class HelpDocCatalogService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val helpDocCatalogRepository: HelpDocCatalogRepository,
    val helpDocRepository: HelpDocRepository,
    val helpDocRelationRepository: HelpDocRelationRepository,
    val helpDocViewRecordRepository: HelpDocViewRecordRepository,
) {


    val all: List<HelpDocCatalogVO>
        /**
         * 查询全部目录
         *
         * @return
         */
        get() = helpDocCatalogRepository.listAll(HelpDocCatalogVO::class)

    /**
     * 添加目录
     *
     * @param helpDocCatalogAddForm
     * @return
     */
    @Synchronized
    fun add(helpDocCatalogAddForm: HelpDocCatalogAddForm): ResponseDTO<String?> {

        val helpDocCatalogs = all
//            .filter { helpDocCatalogAddForm.parentId == it.parentId }
            .filter { helpDocCatalogAddForm.name == it.name }
        if (helpDocCatalogs.isNotEmpty()) {
            return ResponseDTO.userErrorParam("存在相同名称的目录：" + helpDocCatalogs.joinToString(",") { it.name })
        }

        sql.insert(helpDocCatalogAddForm)
        return ResponseDTO.ok()
    }

    /**
     * 更新目录
     *
     * @param updateForm
     * @return
     */
    @Synchronized
    fun update(updateForm: HelpDocCatalogUpdateForm): ResponseDTO<String?> {

        helpDocCatalogRepository.byId(updateForm.helpDocCatalogId) ?: return ResponseDTO.userErrorParam("目录不存在")

        val helpDocCatalogs = all
//            .filter { helpDocCatalogAddForm.parentId == it.parentId }
            .filter { updateForm.helpDocCatalogId != it.helpDocCatalogId }
            .filter { updateForm.name == it.name }
        if (helpDocCatalogs.isNotEmpty()) {
            return ResponseDTO.userErrorParam("存在相同名称的目录：" + helpDocCatalogs.joinToString(",") { it.name })
        }

        sql.update(updateForm)
        return ResponseDTO.ok()
    }

    /**
     * 删除目录（如果有子目录、或者有帮助文档，则不能删除）
     *
     * @param helpDocCatalogId
     * @return
     */
    @Synchronized
    fun delete(helpDocCatalogId: String?): ResponseDTO<String?> {
        if (helpDocCatalogId == null) {
            return ResponseDTO.ok()
        }

        helpDocCatalogRepository.byId(helpDocCatalogId) ?: return ResponseDTO.userErrorParam("目录不存在")

        //如果有子目录，则不能删除
        val helpDocCatalogs = all.filter { helpDocCatalogId == it.parentId }
        if (helpDocCatalogs.isNotEmpty()) {
            return ResponseDTO.userErrorParam("存在子目录：" + helpDocCatalogs.joinToString(",") { it.name })
        }

        //查询是否有帮助文档
        val helpDocVOList: List<HelpDocVO?> = sql.createQuery(HelpDoc::class) {
            where(table.helpDocCatalogId eq helpDocCatalogId)
            select(table.fetch(HelpDocVO::class))
        }.execute()
        if (CollectionUtils.isNotEmpty(helpDocVOList)) {
            return ResponseDTO.userErrorParam("目录下存在文档，不能删除")
        }

        sql.deleteById(HelpDocCatalog::class,helpDocCatalogId)
        return ResponseDTO.ok()
    }
}
