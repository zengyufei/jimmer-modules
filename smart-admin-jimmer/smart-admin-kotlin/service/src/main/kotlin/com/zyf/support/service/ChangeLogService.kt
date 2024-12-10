package com.zyf.support.service

import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.service.dto.ChangeLogAddForm
import com.zyf.service.dto.ChangeLogQueryForm
import com.zyf.service.dto.ChangeLogUpdateForm
import com.zyf.service.dto.ChangeLogVO
import com.zyf.support.ChangeLog
import com.zyf.support.changeLogId
import com.zyf.support.createTime
import com.zyf.support.version
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.babyfish.jimmer.sql.kt.exists
import org.springframework.stereotype.Service

/**
 * 系统更新日志 Service
 *
 * @Author 卓大
 * @Date 2022-09-26 14:53:50
 * @Copyright 1024创新实验室
 */
@Service
class ChangeLogService(
    val sql: KSqlClient
) {
    /**
     * 分页查询
     */
    fun queryPage(
        pageBean: PageBean,
        queryForm: ChangeLogQueryForm
    ): PageResult<ChangeLogVO> {
        val pageResult = sql.createQuery(ChangeLog::class) {

            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(ChangeLogVO::class))
        }.page(pageBean)
        return pageResult
    }

    /**
     * 添加
     */
    @Synchronized
    fun add(addForm: ChangeLogAddForm): ResponseDTO<String?> {
        if (sql.exists(ChangeLog::class) {
                where(table.version eq addForm.version)
            }) {
            return ResponseDTO.userErrorParam("此版本已经存在")
        }

        sql.insert(addForm)
        return ResponseDTO.ok()
    }

    /**
     * 更新
     */
    @Synchronized
    fun update(updateForm: ChangeLogUpdateForm): ResponseDTO<String?> {
        if (sql.exists(ChangeLog::class) {
                where(table.version eq updateForm.version)
                where(table.changeLogId ne updateForm.changeLogId)
            }) {
            return ResponseDTO.userErrorParam("此版本已经存在")
        }
        sql.update(updateForm)
        return ResponseDTO.ok()
    }

    /**
     * 批量删除
     */
    @Synchronized
    fun batchDelete(idList: List<String?>): ResponseDTO<String?> {
        if (idList.isEmpty()) {
            return ResponseDTO.ok()
        }

        sql.deleteByIds(ChangeLog::class, idList)
        return ResponseDTO.ok()
    }

    /**
     * 单个删除
     */
    @Synchronized
    fun delete(changeLogId: String?): ResponseDTO<String?> {
        if (null == changeLogId) {
            return ResponseDTO.ok()
        }

        sql.deleteById(ChangeLog::class, changeLogId)
        return ResponseDTO.ok()
    }

    fun getById(changeLogId: String): ChangeLogVO? {
        return sql.findById(ChangeLogVO::class, changeLogId)
    }
}
