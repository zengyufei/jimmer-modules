package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.code.ErrorCode
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.service.dto.*
import com.zyf.support.*
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.springframework.stereotype.Service

@Slf4j
@Service
class DictService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) {

    /**
     * 所有key
     */
    fun queryAllKey(): List<DictKeyVO> {
        return sql.createQuery(DictKey::class) {
            select(table.fetch(DictKeyVO::class))
        }.execute()
    }

    /**
     * key添加
     */
    fun keyAdd(keyAddForm: DictKeyAddForm): ErrorCode? {
        val fetchOne = sql.createQuery(DictKey::class) {
            where(table.keyCode eq keyAddForm.keyCode)
            select(count(table))
        }.fetchOne()
        if (fetchOne > 0) {
            return UserErrorCode.ALREADY_EXIST
        }
        sql.insert(keyAddForm)
        return null
    }

    /**
     * key 编辑
     */
    fun keyEdit(keyUpdateForm: DictKeyUpdateForm): ErrorCode? {
        val fetchOne = sql.createQuery(DictKey::class) {
            where(table.keyCode eq keyUpdateForm.keyCode)
            where(table.dictKeyId ne keyUpdateForm.dictKeyId)
            select(count(table))
        }.fetchOne()
        if (fetchOne > 0) {
            return UserErrorCode.ALREADY_EXIST
        }

        sql.update(keyUpdateForm)
        return null
    }

    /**
     * key删除
     */
    fun keyDelete(keyIdList: List<String>) {
        if (keyIdList.isEmpty()) {
            return
        }
        sql.deleteByIds(DictKey::class, keyIdList)
    }

    /**
     * 分页查询key
     */
    fun keyQuery(pageBean: PageBean, queryForm: DictKeyQueryForm): PageResult<DictKeyVO> {
        return sql.createQuery(DictKey::class) {
            if (pageBean.sortCode.isNullOrBlank()) {
                orderBy(table.dictKeyId.desc())
            } else {
                orderBy(pageBean)
            }
            where(
                or(
                    table.keyCode `ilike?` queryForm.searchWord,
                    table.keyName `ilike?` queryForm.searchWord
                )
            )
            select(table.fetch(DictKeyVO::class))
        }.page(pageBean)
    }

    /**
     * 值添加
     */
    fun valueAdd(valueAddForm: DictValueAddForm): ErrorCode? {
        val fetchOne = sql.createQuery(DictValue::class) {
            where(table.valueCode eq valueAddForm.valueCode)
            select(count(table))
        }.fetchOne()
        if (fetchOne > 0) {
            return UserErrorCode.ALREADY_EXIST
        }

        sql.insert(valueAddForm)
        return null
    }

    /**
     * 值编辑
     */
    fun valueEdit(valueUpdateForm: DictValueUpdateForm): ErrorCode? {
        sql.findById(DictKey::class, valueUpdateForm.dictKeyId!!) ?: return UserErrorCode.PARAM_ERROR

        val fetchOne = sql.createQuery(DictValue::class) {
            where(table.valueCode eq valueUpdateForm.valueCode)
            where(table.dictValueId ne valueUpdateForm.dictValueId)
            select(count(table))
        }.fetchOne()
        if (fetchOne > 0) {
            return UserErrorCode.ALREADY_EXIST
        }
        sql.update(valueUpdateForm)
        return null
    }

    /**
     * 值删除
     */
    fun valueDelete(valueIdList: List<String?>) {
        if (valueIdList.isEmpty()) {
            return
        }
        sql.deleteByIds(DictValue::class, valueIdList)
    }

    /**
     * 分页查询值
     */
    fun valueQuery(
        pageBean: PageBean,
        queryForm: DictValueQueryForm
    ): PageResult<DictValueVO> {
        return sql.createQuery(DictValue::class) {
            if (pageBean.sortCode.isNullOrBlank()) {
                orderBy(table.dictValueId.desc())
            } else {
                orderBy(pageBean)
            }
            where(table.dictKeyId `eq?` queryForm.dictKeyId)
            where(
                or(
                    table.valueCode `ilike?` queryForm.searchWord,
                    table.valueName `ilike?` queryForm.searchWord
                )
            )
            select(table.fetch(DictValueVO::class))
        }.page(pageBean)
    }
}