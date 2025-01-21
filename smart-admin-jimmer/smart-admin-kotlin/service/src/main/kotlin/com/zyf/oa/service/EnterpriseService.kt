package com.zyf.oa.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.DataTracerTypeEnum
import com.zyf.common.enums.EnterpriseTypeEnum
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.employee.*
import com.zyf.oa.*
import com.zyf.repository.oa.EnterpriseRepository
import com.zyf.service.dto.*
import com.zyf.support.service.DataTracerService
import org.babyfish.jimmer.sql.ast.tuple.Tuple2
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.ast.table.source
import org.babyfish.jimmer.sql.kt.exists
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
class EnterpriseService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val dataTracerService: DataTracerService,
    private val enterpriseRepository: EnterpriseRepository,
) {

    /**
     * 分页查询企业模块
     */
    fun queryByPage(
        pageBean: PageBean,
        queryForm: EnterpriseQueryForm
    ): ResponseDTO<PageResult<EnterpriseVO>> {
        val pageResult = sql.createQuery(Enterprise::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(EnterpriseVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }

    /**
     * 获取导出数据
     */
    fun getExcelExportData(queryForm: EnterpriseQueryForm): List<EnterpriseExcelVO> {
        val vos = sql.createQuery(Enterprise::class) {
            orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(EnterpriseExcelVO::class))
        }.execute()
        return vos
    }

    /**
     * 查询企业详情
     */
    fun getDetail(enterpriseId: String): EnterpriseDetailVO? {
        return sql.findById(EnterpriseDetailVO::class, enterpriseId)
    }

    /**
     * 新建企业
     */
    @Transactional(rollbackFor = [Exception::class])
    fun createEnterprise(createVO: EnterpriseCreateForm): ResponseDTO<String?> {
        // 验证企业名称是否重复
        if (sql.exists(Enterprise::class) {
                where(table.enterpriseName eq createVO.enterpriseName)
            }) {
            return ResponseDTO.userErrorParam("企业名称重复")
        }
        // 数据插入
        val result = sql.insert(createVO)

        val modifiedEntity = result.modifiedEntity
        // 变更记录
        dataTracerService.insert(modifiedEntity.enterpriseId, DataTracerTypeEnum.OA_ENTERPRISE)
        return ResponseDTO.ok()
    }

    /**
     * 编辑企业
     */
    @Transactional(rollbackFor = [Exception::class])
    fun updateEnterprise(updateVO: EnterpriseUpdateForm): ResponseDTO<String?> {
        updateVO.enterpriseId ?: return ResponseDTO.userErrorParam("企业不存在")
        // 验证企业名称是否重复
        if (sql.exists(Enterprise::class) {
                where(table.enterpriseName eq updateVO.enterpriseName)
                where(table.enterpriseId ne updateVO.enterpriseId)
            }) {
            return ResponseDTO.userErrorParam("企业名称重复")
        }

        // 数据编辑
        val result = sql.update(updateVO)


        if (result.isModified) {
            // 变更记录
            val builder = DataTracerForm.Builder()
                .dataId(updateVO.enterpriseId)
                .type(DataTracerTypeEnum.OA_ENTERPRISE.value)
                .content("修改企业信息")
                .diffOld(dataTracerService.getChangeContentK(result.originalEntity))
                .diffNew(dataTracerService.getChangeContentK(result.modifiedEntity))
            dataTracerService.addTrace(builder)
        }
        return ResponseDTO.ok()
    }

    /**
     * 删除企业
     */
    @Transactional(rollbackFor = [Exception::class])
    fun deleteEnterprise(enterpriseId: String): ResponseDTO<String?> {
        // 校验企业是否存在
        val enterpriseDetail = sql.exists(Enterprise::class) { where(table.enterpriseId eq enterpriseId) }
        if (!enterpriseDetail) {
            return ResponseDTO.userErrorParam("企业不存在")
        }
        sql.deleteById(Enterprise::class, enterpriseId)
        // 变更记录
        dataTracerService.delete(enterpriseId, DataTracerTypeEnum.OA_ENTERPRISE)
        return ResponseDTO.ok()
    }

    /**
     * 企业列表查询
     */
    fun queryList(type: EnterpriseTypeEnum?): ResponseDTO<List<EnterpriseListVO>> {
        val vos = sql.createQuery(Enterprise::class) {
            where(table.type `eq?` type)
            where(table.disabledFlag `eq?` false)
            select(table.fetch(EnterpriseListVO::class))
        }.execute()
        return ResponseDTO.ok(vos)
    }

    //----------------------------------------- 以下为员工相关--------------------------------------------

    /**
     * 企业添加员工
     */
    @Synchronized
    @Transactional(rollbackFor = [Exception::class])
    fun addEmployee(enterpriseEmployeeForm: EnterpriseEmployeeForm): ResponseDTO<String?> {
        val enterpriseId = enterpriseEmployeeForm.enterpriseId
        val waitAddEmployeeIdList = enterpriseEmployeeForm.employeeIdList

        sql.findById(Enterprise::class, enterpriseId) ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)

        if (waitAddEmployeeIdList.isNotEmpty()) {
            val list = mutableListOf<Tuple2<String, String>>()
            waitAddEmployeeIdList.forEach {
                list.add(Tuple2(it, enterpriseId))
            }
            sql.getAssociations(Employee::enterprise)
                .insertAllIfAbsent(list)
        }
        return ResponseDTO.ok()
    }

    /**
     * 企业删除员工
     */
    @Synchronized
    fun deleteEmployee(enterpriseEmployeeForm: EnterpriseEmployeeForm): ResponseDTO<String?> {
        val enterpriseId = enterpriseEmployeeForm.enterpriseId
        val waitDeleteEmployeeIdList = enterpriseEmployeeForm.employeeIdList

        sql.findById(Enterprise::class, enterpriseId) ?: return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST)


        if (waitDeleteEmployeeIdList.isNotEmpty()) {
            val list = mutableListOf<Tuple2<String, String>>()
            waitDeleteEmployeeIdList.forEach {
                list.add(Tuple2(it, enterpriseId))
            }
            sql.getAssociations(Employee::enterprise)
                .deleteAll(list)
        }

        return ResponseDTO.ok()
    }

    /**
     * 企业下员工列表
     */
    fun employeeList(enterpriseIdList: List<String>): List<EnterpriseEmployeeVO> {
        if (enterpriseIdList.isEmpty()) {
            return emptyList()
        }
        return sql.createQuery(Employee::class) {
            enterpriseIdList.takeIf { it.isNotEmpty() }?.let {
                where(table.enterpriseId `valueIn?` enterpriseIdList)
            }
            select(table.fetch(EnterpriseEmployeeVO::class))
        }.execute()
    }

    /**
     * 分页查询企业员工
     */
    fun queryPageEmployeeList(pageBean: PageBean, queryForm: EnterpriseEmployeeQueryForm): PageResult<EnterpriseEmployeeVO> {
        return sql.createQuery(Employee::class) {
            orderBy(pageBean)
            where(queryForm)
            select(table.fetch(EnterpriseEmployeeVO::class))
        }.page(pageBean)
    }
}