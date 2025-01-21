package com.zyf.support.service;

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.oa.*
import com.zyf.repository.support.SerialNumberRepository
import com.zyf.service.dto.*
import com.zyf.support.SerialNumber
import com.zyf.support.createTime
import com.zyf.support.lastTime
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 单号生成器定义表(SerialNumber)表服务实现类
 *
 * @author makejava
 * @since 2024-12-09 20:36:31
 */
@Slf4j
@Service("serialNumberService")
class SerialNumberService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val serialNumberRepository: SerialNumberRepository,
) {


    /**
     * 分页查询单号生成器定义表数据
     *
     * @param pageBean 分页信息，包括当前页码和每页大小
     * @param queryForm 查询条件，用于筛选单号生成器定义表数据
     * @return 包含分页结果的响应对象，其中包含单号生成器定义表数据列表和分页信息
     */
    fun queryByPage(pageBean: PageBean, queryForm: SerialNumberQueryForm): ResponseDTO<PageResult<SerialNumberVO>> {
        val pageResult = sql.createQuery(SerialNumber::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(SerialNumberVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 查询单号生成器定义表数据列表，不包含分页
     *
     * @param queryForm 查询条件，用于筛选单号生成器定义表数据
     * @return 包含单号生成器定义表列表的响应对象
     */
    fun queryList(queryForm: SerialNumberQueryForm): ResponseDTO<List<SerialNumberVO>> {
        val vos = sql.createQuery(SerialNumber::class) {

            where(queryForm)
            select(table.fetch(SerialNumberVO::class))
        }.execute()
        return ResponseDTO.ok(vos)
    }

    /**
     * 新增单号生成器定义表信息
     *
     * @param serialNumberAddParam 新增单号生成器定义表的信息表单
     * @return 响应对象，包含新增单号生成器定义表的ID
     */
    @Transactional(rollbackFor = [Exception::class])
    fun add(addParam: SerialNumberAddParam): ResponseDTO<String?> {
        // 数据插入
        val result = sql.insert(addParam)
        return ResponseDTO.ok()
    }

    /**
     * 编辑单号生成器定义表信息
     *
     * @param updateVO 更新单号生成器定义表的信息表单，包含需要修改的字段
     * @return 响应对象，包含更新结果的提示信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun update(updateParam: SerialNumberUpdateParam): ResponseDTO<String?> {
        // 数据编辑
        sql.update(updateParam)
        return ResponseDTO.ok()
    }

    /**
     * 删除指定ID的单号生成器定义表
     *
     * @param serialNumberId 单号生成器定义表唯一标识符
     * @return 响应对象，包含删除结果的提示信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun delete(serialNumberId: String): ResponseDTO<String?> {
        sql.deleteById(SerialNumber::class, serialNumberId)
        return ResponseDTO.ok()
    }

    /**
     * 根据单号生成器定义表ID查询单号生成器定义表详细信息
     *
     * @param serialNumberId 单号生成器定义表唯一标识符
     * @return 单号生成器定义表详细信息对象，如果找不到则返回null
     */
    fun getDetail(serialNumberId: String): SerialNumberDetailVO? {
        return sql.findById(SerialNumberDetailVO::class, serialNumberId)
    }

    /**
     * 导出单号生成器定义表数据到Excel表格
     *
     * @param queryForm 查询条件，用于筛选要导出的单号生成器定义表数据
     * @return 包含导出数据的列表，格式化为Excel表格的数据结构
     */
    fun getExcelExportData(queryForm: SerialNumberQueryForm): List<SerialNumberExcelVO> {
        val vos = sql.createQuery(SerialNumber::class) {
            orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(SerialNumberExcelVO::class))
        }.execute()
        return vos
    }

    fun listAll(): List<SerialNumber> {
        return serialNumberRepository.listAll() {
            orderBy(table.lastTime.desc())
        }
    }

}
