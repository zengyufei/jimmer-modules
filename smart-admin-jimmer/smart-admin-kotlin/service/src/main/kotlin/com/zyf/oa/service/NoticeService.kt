package com.zyf.oa.service

import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.DataTracerTypeEnum
import com.zyf.common.enums.NoticeVisibleRangeDataTypeEnum
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.employee.addBy
import com.zyf.oa.Notice
import com.zyf.oa.by
import com.zyf.oa.domain.NoticeVisibleRangeVO
import com.zyf.oa.noticeId
import com.zyf.oa.publishTime
import com.zyf.service.dto.*
import com.zyf.support.service.DataTracerService
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.exists
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class NoticeService(
    val sql: KSqlClient,
    val noticeTypeService: NoticeTypeService,
    val dataTracerService: DataTracerService,
) {

    fun query(pageBean: PageBean, queryForm: NoticeQueryForm): PageResult<NoticeVO> {
        val pageResult = sql.createQuery(Notice::class) {
            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.publishTime.desc(), table.noticeId.desc())

            where(queryForm)
            select(table.fetch(NoticeVO::class))
        }.page(pageBean)
        return pageResult
    }

    @Transactional(rollbackFor = [Exception::class])
    fun add(addForm: NoticeAddForm): ResponseDTO<String?> {

        noticeTypeService.getByNoticeTypeId(addForm.noticeTypeId!!) ?: return ResponseDTO.userErrorParam("分类不存在")

        if (addForm.allVisibleFlag) {
            return ResponseDTO.ok()
        }

        val visibleRangeUpdateList = addForm.visibleRangeList
        if (visibleRangeUpdateList.isEmpty()) {
            return ResponseDTO.userErrorParam("未设置可见范围")
        }
        val notice = addForm.toEntity {
            this.publishTime = if (!this.scheduledPublishFlag) LocalDateTime.now() else this.publishTime
            addForm.visibleRangeList
                .filter { it.dataType == NoticeVisibleRangeDataTypeEnum.DEPARTMENT.value }
                .mapNotNull { it.dataId }
                .forEach {
                    this.departmentRanges().addBy {
                        this.departmentId = it
                    }
                }
            addForm.visibleRangeList
                .filter { it.dataType == NoticeVisibleRangeDataTypeEnum.EMPLOYEE.value }
                .mapNotNull { it.dataId }
                .forEach {
                    this.employeeRanges().addBy {
                        this.employeeId = it
                    }
                }
        }
        val result = sql.insert(notice)
        val noticeId = result.modifiedEntity.noticeId
        dataTracerService.insert(noticeId, DataTracerTypeEnum.OA_NOTICE)

        return ResponseDTO.ok()
    }

    fun update(updateForm: NoticeUpdateForm): ResponseDTO<String?> {
        sql.findById(Notice::class, updateForm.noticeId)
            ?: return ResponseDTO.userErrorParam("通知不存在")

        noticeTypeService.byId(updateForm.noticeTypeId!!) ?: return ResponseDTO.userErrorParam("分类不存在")

        if (updateForm.allVisibleFlag) {
            return ResponseDTO.ok()
        }

        val visibleRangeUpdateList = updateForm.visibleRangeList
        if (visibleRangeUpdateList.isEmpty()) {
            return ResponseDTO.userErrorParam("未设置可见范围")
        }

        val notice = updateForm.toEntity {
            this.publishTime = if (!this.scheduledPublishFlag) LocalDateTime.now() else this.publishTime
            updateForm.visibleRangeList
                .filter { it.dataType == NoticeVisibleRangeDataTypeEnum.DEPARTMENT.value }
                .mapNotNull { it.dataId }
                .forEach {
                    this.departmentRanges().addBy {
                        this.departmentId = it
                    }
                }
            updateForm.visibleRangeList
                .filter { it.dataType == NoticeVisibleRangeDataTypeEnum.EMPLOYEE.value }
                .mapNotNull { it.dataId }
                .forEach {
                    this.employeeRanges().addBy {
                        this.employeeId = it
                    }
                }
        }

        sql.update(notice)

        return ResponseDTO.ok()
    }

    fun delete(noticeId: String): ResponseDTO<String?> {
        if (!sql.exists(Notice::class) {
                where(table.noticeId eq noticeId)
            }
        ) {
            return ResponseDTO.userErrorParam("通知公告不存在")
        }
        sql.deleteById(Notice::class, noticeId)
        dataTracerService.delete(noticeId, DataTracerTypeEnum.OA_NOTICE)
        return ResponseDTO.ok()
    }


    fun getUpdateFormVO(noticeId: String): NoticeUpdateFormVO? {
        val notice = sql.findById(newFetcher(Notice::class).by {
            allScalarFields()
            noticeType()
            departmentRanges {
                allScalarFields()
            }
            employeeRanges {
                allScalarFields()
            }
        }, noticeId) ?: return null

        val employeeList = notice.employeeRanges.map {
            val noticeVisibleRangeVO = NoticeVisibleRangeVO()
            noticeVisibleRangeVO.dataType = NoticeVisibleRangeDataTypeEnum.EMPLOYEE.value
            noticeVisibleRangeVO.dataId = it.employeeId
            noticeVisibleRangeVO.dataName = it.actualName
            noticeVisibleRangeVO
        }
        val departmentLiist = notice.departmentRanges.map {
            val noticeVisibleRangeVO = NoticeVisibleRangeVO()
            noticeVisibleRangeVO.dataType = NoticeVisibleRangeDataTypeEnum.DEPARTMENT.value
            noticeVisibleRangeVO.dataId = it.departmentId
            noticeVisibleRangeVO.dataName = it.departmentName
            noticeVisibleRangeVO
        }


        val noticeUpdateFormVO = NoticeUpdateFormVO(notice)
        return noticeUpdateFormVO.copy(
            visibleRangeList = departmentLiist + employeeList
        )
    }

}