package com.zyf.helpDoc.service.impl;

import com.zyf.helpDoc.HelpDocViewRecord;
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
 * 帮助文档-查看记录(HelpDocViewRecord)表服务实现类
 *
 * @author makejava
 * @since 2024-12-14 16:24:53
 */
@Slf4j
@Service("helpDocViewRecordService")
class HelpDocViewRecordService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val helpDocViewRecordRepository: HelpDocViewRecordRepository,
) {


    /**
     * 分页查询帮助文档-查看记录数据
     *
     * @param pageBean 分页信息，包括当前页码和每页大小
     * @param queryForm 查询条件，用于筛选帮助文档-查看记录数据
     * @return 包含分页结果的响应对象，其中包含帮助文档-查看记录数据列表和分页信息
     */
    fun queryByPage(pageBean: PageBean, queryForm: HelpDocViewRecordQueryForm): ResponseDTO<PageResult<HelpDocViewRecordVO>> {
        val pageResult = sql.createQuery(HelpDocViewRecord::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(HelpDocViewRecordVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 查询帮助文档-查看记录数据列表，不包含分页
     *
     * @param queryForm 查询条件，用于筛选帮助文档-查看记录数据
     * @return 包含帮助文档-查看记录列表的响应对象
     */
    fun queryList(queryForm: HelpDocViewRecordQueryForm): ResponseDTO<List<HelpDocViewRecordVO>> {
        val vos = sql.createQuery(HelpDocViewRecord::class) {

            where(queryForm)
            select(table.fetch(HelpDocViewRecordVO::class))
        }.execute()
        return ResponseDTO.ok(vos)
    }

    /**
     * 新增帮助文档-查看记录信息
     *
     * @param helpDocViewRecordAddParam 新增帮助文档-查看记录的信息表单
     * @return 响应对象，包含新增帮助文档-查看记录的ID
     */
    @Transactional(rollbackFor = [Exception::class])
    fun add(addParam: HelpDocViewRecordAddParam): ResponseDTO<String?> {
        // 数据插入
        val result = sql.insert(addParam)
        return ResponseDTO.ok()
    }

    /**
     * 编辑帮助文档-查看记录信息
     *
     * @param updateVO 更新帮助文档-查看记录的信息表单，包含需要修改的字段
     * @return 响应对象，包含更新结果的提示信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun update(updateParam: HelpDocViewRecordUpdateParam): ResponseDTO<String?> {
        // 数据编辑
        val result = sql.update(updateParam)
        return ResponseDTO.ok()
    }

    /**
     * 删除指定ID的帮助文档-查看记录
     *
     * @param helpDocId 帮助文档-查看记录唯一标识符
     * @return 响应对象，包含删除结果的提示信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun delete(helpDocViewRecordId: String): ResponseDTO<String?> {
        sql.deleteById(HelpDocViewRecord::class, helpDocViewRecordId)
        return ResponseDTO.ok()
    }

    /**
     * 根据帮助文档-查看记录ID查询帮助文档-查看记录详细信息
     *
     * @param helpDocViewRecordId 帮助文档-查看记录唯一标识符
     * @return 帮助文档-查看记录详细信息对象，如果找不到则返回null
     */
    fun getDetail(helpDocViewRecordId: String): HelpDocViewRecordDetailVO? {
        return sql.findById(HelpDocViewRecordDetailVO::class, helpDocViewRecordId)
    }

    /**
     * 导出帮助文档-查看记录数据到Excel表格
     *
     * @param queryForm 查询条件，用于筛选要导出的帮助文档-查看记录数据
     * @return 包含导出数据的列表，格式化为Excel表格的数据结构
     */
    fun getExcelExportData(queryForm: HelpDocViewRecordQueryForm): List<HelpDocViewRecordExcelVO> {
        val vos = sql.createQuery(HelpDocViewRecord::class) {
            orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(HelpDocViewRecordExcelVO::class))
        }.execute()
        return vos
    }

}
