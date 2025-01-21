package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.constant.DataTracerConst
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.RequestUser
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.DataTracerTypeEnum
import com.zyf.common.enums.UserTypeEnum
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.common.utils.SmartIpUtil
import com.zyf.common.utils.SmartRequestUtil
import com.zyf.login.domain.RequestEmployee
import com.zyf.service.dto.DataTracerForm
import com.zyf.service.dto.DataTracerQueryForm
import com.zyf.service.dto.DataTracerVO
import com.zyf.support.DataTracer
import com.zyf.support.dataTracerId
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.springframework.stereotype.Service

/**
 * 数据变动记录 Service
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-07-23 19:38:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Slf4j
@Service
class DataTracerService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val dataTracerChangeContentService: DataTracerChangeContentService,
) {


    /**
     * 获取变更内容
     *
     * @param value
     * @return
     */
    fun getChangeContentK(value: Any?): String? {
        return value?.let { dataTracerChangeContentService.getChangeContentK(it) }
    }


//
//    /**
//     * 保存【修改】数据变动记录
//     *
//     * @param dataId
//     * @param type
//     */
//    fun update(dataId: String, type: DataTracerTypeEnum, oldObject: Any?, newObject: Any?) {
//        val form = DataTracerForm.Builder()
//            .dataId(dataId)
//            .type(type.value)
//            .content(dataTracerChangeContentService.getChangeContent(oldObject, newObject))
//            .build()
//        this.addTrace(form)
//    }


    /**
     * 保存【新增】数据变动记录
     *
     * @param dataId
     * @param type
     */
    fun insert(dataId: String, type: DataTracerTypeEnum) {
        val form = DataTracerForm.Builder()
            .dataId(dataId)
            .type(type.value)
            .content(DataTracerConst.INSERT)
        this.addTrace(form)
    }

    /**
     * 保存【删除】数据变动记录
     *
     * @param dataId
     * @param type
     */
    fun delete(dataId: String, type: DataTracerTypeEnum) {
        val form = DataTracerForm.Builder()
            .dataId(dataId)
            .type(type.value)
            .content(DataTracerConst.DELETE)
        this.addTrace(form)
    }


    /**
     * 保存【批量删除】数据变动记录
     *
     * @param dataIdList
     * @param type
     */
    fun batchDelete(dataIdList: List<String>, type: DataTracerTypeEnum) {
        if (dataIdList.isEmpty()) {
            return
        }

        this.addTraceList(
            dataIdList.map {
                DataTracerForm.Builder()
                    .dataId(it)
                    .type(type.value)
                    .content(DataTracerConst.DELETE)
            }.toMutableList()
        )
    }

    /**
     * 保存数据变动记录
     *
     * @param dataId
     * @param type
     * @param content
     */
    fun addTrace(dataId: String, type: DataTracerTypeEnum, content: String?) {
        val form = DataTracerForm.Builder()
            .dataId(dataId)
            .type(type.value)
            .content(content)
        this.addTrace(form)
    }


    /**
     * 保存数据变动记录
     */
    fun addTrace(tracerFormBuild: DataTracerForm.Builder) {
        val requestUser = SmartRequestUtil.requestUser!!
        this.addTrace(tracerFormBuild, requestUser)
    }


    /**
     * 保存数据变动记录
     */
    fun addTrace(tracerForm: DataTracerForm.Builder, requestUser: RequestUser?) {
        requestUser?.let {
            tracerForm.ip(requestUser.ip)
            tracerForm.ip(SmartIpUtil.getRegion(requestUser.ip))
            tracerForm.userAgent(requestUser.userAgent)
            tracerForm.userId(requestUser.userId)
            tracerForm.userType(requestUser.userType.value)
            tracerForm.userName(requestUser.userName)
        }
        sql.insert(tracerForm.build())
    }

    /**
     * 批量保存数据变动记录
     */
    fun addTraceList(tracerFormList: List<DataTracerForm.Builder>) {
        val requestUser = SmartRequestUtil.requestUser!!
        this.addTraceList(tracerFormList, requestUser)
    }

    /**
     * 批量保存数据变动记录
     */
    fun addTraceList(tracerFormList: List<DataTracerForm.Builder>, requestUser: RequestUser?) {
        if (tracerFormList.isEmpty()) {
            return
        }
        val builders = requestUser?.let {
            tracerFormList.map {
                it.ip(requestUser.ip)
                it.ip(SmartIpUtil.getRegion(requestUser.ip))
                it.userAgent(requestUser.userAgent)
                it.userId(requestUser.userId)
                it.userType(requestUser.userType.value)
                it.userName(requestUser.userName)
                it.build()
            }
        } ?: tracerFormList.map { it.build() }
        sql.entities.saveEntities(builders)
    }


    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    fun query(pageBean: PageBean, queryForm: DataTracerQueryForm): ResponseDTO<PageResult<DataTracerVO>> {
        val pageResult = sql.createQuery(DataTracer::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.dataTracerId.desc())

            where(queryForm)
            select(table.fetch(DataTracerVO::class))
        }.page(pageBean)
        return ResponseDTO.ok(pageResult)
    }
}
