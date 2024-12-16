package com.zyf.helpDoc.service.impl;

import com.zyf.helpDoc.HelpDocCatalog;
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.oa.*
import com.zyf.service.dto.*
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 帮助文档-目录(HelpDocCatalog)表服务实现类
 *
 * @author makejava
 * @since 2024-12-14 16:48:23
 */
@Slf4j
@Service("helpDocCatalogService")
class HelpDocCatalogService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val helpDocCatalogRepository: HelpDocCatalogRepository,
) {


    /**
     * 分页查询帮助文档-目录数据
     *
     * @param pageBean 分页信息，包括当前页码和每页大小
     * @param queryForm 查询条件，用于筛选帮助文档-目录数据
     * @return 包含分页结果的响应对象，其中包含帮助文档-目录数据列表和分页信息
     */
    fun queryByPage(pageBean: PageBean, queryForm: HelpDocCatalogQueryForm): ResponseDTO<PageResult<HelpDocCatalogVO>> {
        val pageResult = sql.createQuery(HelpDocCatalog::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(HelpDocCatalogVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 查询帮助文档-目录数据列表，不包含分页
     *
     * @param queryForm 查询条件，用于筛选帮助文档-目录数据
     * @return 包含帮助文档-目录列表的响应对象
     */
    fun queryList(queryForm: HelpDocCatalogQueryForm): ResponseDTO<List<HelpDocCatalogVO>> {
        val vos = sql.createQuery(HelpDocCatalog::class) {

            where(queryForm)
            select(table.fetch(HelpDocCatalogVO::class))
        }.execute()
        return ResponseDTO.ok(vos)
    }

    /**
     * 新增帮助文档-目录信息
     *
     * @param helpDocCatalogAddParam 新增帮助文档-目录的信息表单
     * @return 响应对象，包含新增帮助文档-目录的ID
     */
    @Transactional(rollbackFor = [Exception::class])
    fun add(addParam: HelpDocCatalogAddParam): ResponseDTO<String?> {
        // 数据插入
        val result = sql.insert(addParam)
        return ResponseDTO.ok()
    }

    /**
     * 编辑帮助文档-目录信息
     *
     * @param updateVO 更新帮助文档-目录的信息表单，包含需要修改的字段
     * @return 响应对象，包含更新结果的提示信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun update(updateParam: HelpDocCatalogUpdateParam): ResponseDTO<String?> {
        // 数据编辑
        val result = sql.update(updateParam)
        return ResponseDTO.ok()
    }

    /**
     * 删除指定ID的帮助文档-目录
     *
     * @param helpDocCatalogId 帮助文档-目录唯一标识符
     * @return 响应对象，包含删除结果的提示信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun delete(helpDocCatalogId: String): ResponseDTO<String?> {
        sql.deleteById(HelpDocCatalog::class, helpDocCatalogId)
        return ResponseDTO.ok()
    }

    /**
     * 根据帮助文档-目录ID查询帮助文档-目录详细信息
     *
     * @param helpDocCatalogId 帮助文档-目录唯一标识符
     * @return 帮助文档-目录详细信息对象，如果找不到则返回null
     */
    fun getDetail(helpDocCatalogId: String): HelpDocCatalogDetailVO? {
        return sql.findById(HelpDocCatalogDetailVO::class, helpDocCatalogId)
    }

    /**
     * 导出帮助文档-目录数据到Excel表格
     *
     * @param queryForm 查询条件，用于筛选要导出的帮助文档-目录数据
     * @return 包含导出数据的列表，格式化为Excel表格的数据结构
     */
    fun getExcelExportData(queryForm: HelpDocCatalogQueryForm): List<HelpDocCatalogExcelVO> {
        val vos = sql.createQuery(HelpDocCatalog::class) {
            orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(HelpDocCatalogExcelVO::class))
        }.execute()
        return vos
    }

}
