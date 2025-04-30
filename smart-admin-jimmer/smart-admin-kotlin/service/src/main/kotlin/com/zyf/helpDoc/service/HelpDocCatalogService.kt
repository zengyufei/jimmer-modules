package com.zyf.helpDoc.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.list
import com.zyf.helpDoc.HelpDoc
import com.zyf.helpDoc.HelpDocCatalog
import com.zyf.helpDoc.helpDocCatalogId
import com.zyf.service.dto.HelpDocCatalogAddForm
import com.zyf.service.dto.HelpDocCatalogUpdateForm
import com.zyf.service.dto.HelpDocCatalogVO
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.exists
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
) {


    val all: List<HelpDocCatalogVO>
        /**
         * 查询全部目录
         *
         * @return
         */
        get() = sql.list(HelpDocCatalog::class, HelpDocCatalogVO::class)

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

        sql.findById(HelpDocCatalog::class, updateForm.helpDocCatalogId) ?: return ResponseDTO.userErrorParam("目录不存在")

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
        helpDocCatalogId ?: return ResponseDTO.ok()

        sql.findById(HelpDocCatalog::class, helpDocCatalogId) ?: return ResponseDTO.userErrorParam("目录不存在")

        //如果有子目录，则不能删除
        val helpDocCatalogs = all.filter { helpDocCatalogId == it.parentId }
        if (helpDocCatalogs.isNotEmpty()) {
            return ResponseDTO.userErrorParam("存在子目录：" + helpDocCatalogs.joinToString(",") { it.name })
        }

        //查询是否有帮助文档
        if (sql.exists(HelpDoc::class) {
                where(table.helpDocCatalogId eq helpDocCatalogId)
            }) {
            return ResponseDTO.userErrorParam("目录下存在文档，不能删除")
        }

        sql.deleteById(HelpDocCatalog::class, helpDocCatalogId)
        return ResponseDTO.ok()
    }
}
